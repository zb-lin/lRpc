package com.lzb.spring;

import com.lzb.annotation.RpcReference;
import com.lzb.annotation.RpcService;
import com.lzb.config.RpcServiceConfig;
import com.lzb.provider.ServiceProvider;
import com.lzb.proxy.RpcClientProxy;
import com.lzb.registry.nacos.util.NacosUtils;
import com.lzb.remoting.client.RpcClient;
import com.lzb.serviceloader.ServiceLoader;
import com.lzb.threadpool.RpcConfig;
import com.lzb.threadpool.ThreadPoolFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import static com.lzb.remoting.constants.RpcConstants.PORT;

/**
 * 扫描获取服务提供类和消费端注入
 */
@Slf4j
@Component
public class SpringBeanPostProcessor implements BeanPostProcessor {

    /**
     * 注册中心 默认为zk
     */
    private final ServiceProvider serviceProvider;
    /**
     * 请求处理 默认 netty
     */
    private final RpcClient rpcClient;

    public SpringBeanPostProcessor() {
        this.serviceProvider = ServiceLoader.getServiceLoader(ServiceProvider.class).getService(RpcConfig.getRpcConfig().getServiceProvider());
        this.rpcClient = ServiceLoader.getServiceLoader(RpcClient.class).getService(RpcConfig.getRpcConfig().getRpcClient());
    }

    @SneakyThrows
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(RpcService.class)) {
            log.info("[{}] service discovered", bean.getClass().getName());
            RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                    .service(bean).build();
            serviceProvider.publishService(rpcServiceConfig);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = bean.getClass();
        Field[] declaredFields = targetClass.getDeclaredFields();
        for (Field field : declaredFields) {
            RpcReference rpcReference = field.getAnnotation(RpcReference.class);
            // 注入代理类
            if (rpcReference != null) {
                RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient);
                Object clientProxy = rpcClientProxy.getProxy(field.getType());
                field.setAccessible(true);
                try {
                    field.set(bean, clientProxy);
                } catch (IllegalAccessException e) {
                    log.error(e.getMessage());
                }
            }
        }
        return bean;
    }

    @PreDestroy
    public void destroy() {
        try {
            InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), PORT);
//            CuratorUtils.clearRegistry(CuratorUtils.getZkClient(), inetSocketAddress);
            NacosUtils.clearRegistry(inetSocketAddress);
        } catch (UnknownHostException ignored) {
        }
        ThreadPoolFactory.shutDownAllThreadPool();
    }
}
