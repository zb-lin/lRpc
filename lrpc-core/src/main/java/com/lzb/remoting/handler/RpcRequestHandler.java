package com.lzb.remoting.handler;

import com.lzb.exception.RpcException;
import com.lzb.provider.ServiceProvider;
import com.lzb.remoting.dto.RpcRequest;
import com.lzb.serviceloader.ServiceLoader;
import com.lzb.config.RpcConfig;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * rpc 方法执行器
 */
@Slf4j
public class RpcRequestHandler {
    private final ServiceProvider serviceProvider;

    public RpcRequestHandler() {
        serviceProvider = ServiceLoader.getServiceLoader(ServiceProvider.class).getService(RpcConfig.getRpcConfig().getServiceProvider());
    }

    /**
     * 执行目标方法
     */
    public Object handle(RpcRequest rpcRequest) {
        Object service = serviceProvider.getService(rpcRequest.getRpcServiceName());
        Object result;
        try {
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            result = method.invoke(service, rpcRequest.getParameters());
            log.info("service:[{}] execution method successful:[{}]", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException |
                 IllegalAccessException e) {
            throw new RpcException(e.getMessage(), e);
        }
        return result;
    }
}
