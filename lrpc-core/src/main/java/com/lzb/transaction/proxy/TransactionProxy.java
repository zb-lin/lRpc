package com.lzb.transaction.proxy;

import com.lzb.transaction.annotation.GlobalTransaction;
import com.lzb.transaction.database.config.RequestContext;
import com.lzb.transaction.database.config.RequestStatusEnum;
import com.lzb.transaction.database.pool.DefaultDataSource;
import com.lzb.transaction.database.util.JDBCUtils;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.UUID;


@Slf4j

public class TransactionProxy implements MethodInterceptor {

    private DefaultDataSource defaultDataSource;

    public <T> T getProxy(Class<T> targetClass) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(targetClass);
        enhancer.setCallback(this);
        return targetClass.cast(enhancer.create());
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        if (!method.isAnnotationPresent(GlobalTransaction.class)) {
            return methodProxy.invokeSuper(o, objects);
        }
        // 创建一条记录 开启事务 状态为未完成 --->  执行方法  --->   结束事务
        // 唯一id   -->  本地异常 远端回溯    远端异常  本地回溯
        // 异常  1. 远程前 直接回滚   2. 远程  出现异常修改表状态为error, 读取后全部回滚
        if (defaultDataSource == null) {
            defaultDataSource = DefaultDataSource.getDefaultDataSource();
        }
        Connection connection = defaultDataSource.getConnection();
        connection.setAutoCommit(false);
        log.info("client start transaction");
        RequestContext requestContext = RequestContext.getInstance();
        String requestId = UUID.randomUUID().toString();
        requestContext.setContext(requestId);
        Object result = null;
        JDBCUtils.insert(requestId);
        try {
            result = methodProxy.invokeSuper(o, objects);
            for (int i = 0; i < 3; i++) {
                // 方法执行后日志表状态没有出现error直接提交, 修改日志表为success, 提醒远程提交
                if (JDBCUtils.queryCount(requestId)) {
                    connection.commit();
                    JDBCUtils.update(RequestStatusEnum.SUCCESS.getStatus(), requestId);
                    log.info("Request normal execution");
                    break;
                }
                log.error("Request execution exception");
                result = methodProxy.invokeSuper(o, objects);
            }
            if (JDBCUtils.queryCount(requestId)) {
                connection.commit();
                JDBCUtils.update(RequestStatusEnum.SUCCESS.getStatus(), requestId);
                log.info("Request normal execution");
            } else {
                // 出现error, 回滚
                connection.rollback();
                log.error("Request execution exception");
            }
            log.info("client end transaction");
        } catch (Exception e) {
            JDBCUtils.update(e.getMessage(), e.getMessage(), RequestStatusEnum.ERROR.getStatus(), requestId);
            connection.rollback();
            log.info("handler failed", e);
        } finally {
            defaultDataSource.closeConnection();
        }
        return result;
    }
}
