package com.lzb.remoting.handler;

import com.lzb.config.RpcConfig;
import com.lzb.enums.FailureStrategyEnum;
import com.lzb.exception.RpcException;
import com.lzb.flowcontrol.FlowControl;
import com.lzb.provider.ServiceProvider;
import com.lzb.remoting.dto.RpcRequest;
import com.lzb.serviceloader.ServiceLoader;
import com.lzb.threadpool.ThreadPoolFactory;
import com.lzb.transaction.database.config.RequestStatusEnum;
import com.lzb.transaction.database.pool.DefaultDataSource;
import com.lzb.transaction.database.util.JDBCUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * rpc 方法执行器
 */
@Slf4j
public class RpcRequestHandler {
    private final ServiceProvider serviceProvider;
    private final FlowControl flowControl;
    private final RpcConfig rpcConfig = RpcConfig.getRpcConfig();
    private final DefaultDataSource defaultDataSource = DefaultDataSource.getDefaultDataSource();
    private final ExecutorService threadPool = ThreadPoolFactory.createThreadPool("handle-service");

    public RpcRequestHandler() {
        serviceProvider = ServiceLoader.getServiceLoader(ServiceProvider.class).getService(rpcConfig.getServiceProvider());
        flowControl = ServiceLoader.getServiceLoader(FlowControl.class).getService(rpcConfig.getFlowControl());
    }

    /**
     * 执行目标方法
     */
    public Object handle(RpcRequest rpcRequest) {
        try {
            Connection connection = defaultDataSource.getConnection();
            connection.setAutoCommit(false);
            log.info("server start transaction");
            Object result = null;
            try {
                result = execute(rpcRequest);
                // 正常执行, 先返回结果, 异步提交
                CompletableFuture.runAsync(() -> {
                    int status;
                    // 轮询日志表, 出现error或success退出
                    while ((status = JDBCUtils.queryResult(rpcRequest.getRequestId())) == RequestStatusEnum.PROCESSING.getStatus()) {
                        try {
                            TimeUnit.SECONDS.sleep(3);
                        } catch (InterruptedException e) {
                            throw new RpcException("database handler error", e);
                        }
                    }
                    try {
                        // 调用方成功, 远程提交
                        if (status == RequestStatusEnum.SUCCESS.getStatus()) {
                            connection.commit();
                        } else if (status == RequestStatusEnum.ERROR.getStatus()) {
                            // 其他出现异常, 进行回滚
                            connection.rollback();
                        }
                    } catch (SQLException e) {
                        throw new RpcException("database handler error", e);
                    }
                }, threadPool);
                log.info("server end transaction");
            } catch (Exception e) {
                // 出现异常, 修改为error, 全部回滚
                connection.rollback();
                JDBCUtils.update(e.getMessage(), e.getMessage(), RequestStatusEnum.ERROR.getStatus(), rpcRequest.getRequestId());
                log.info("handler failed", e);
            } finally {
                defaultDataSource.closeConnection();
            }
            return result;
        } catch (SQLException e) {
            throw new RpcException("database handler error", e);
        }
    }

    public Object execute(RpcRequest rpcRequest) {
        String rpcServiceName = rpcRequest.getRpcServiceName();
        String methodName = rpcRequest.getMethodName();
        Object service = serviceProvider.getService(rpcServiceName);
        String flowControl = rpcConfig.getFlowControl();
        String failureStrategy = rpcConfig.getFailureStrategy();
        log.info("flowControl [{}] failureStrategy [{}]", flowControl, failureStrategy);
        if (FailureStrategyEnum.WAIT.getName().equals(failureStrategy)) {
            this.flowControl.doFlowControlWithWait(service.getClass().getName());
        } else if (FailureStrategyEnum.FAIL_FAST.getName().equals(failureStrategy)
                && this.flowControl.doFlowControlWithFailFast(service.getClass().getName())) {
            return new Object();
        }
        Object result;
        try {
            Method method = service.getClass().getMethod(methodName, rpcRequest.getParamTypes());
            result = method.invoke(service, rpcRequest.getParameters());
            log.info("service:[{}] execution method successful:[{}]", rpcServiceName, methodName);
        } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException |
                 IllegalAccessException e) {
            throw new RpcException(e.getMessage(), e);
        }
        return result;
    }
}
