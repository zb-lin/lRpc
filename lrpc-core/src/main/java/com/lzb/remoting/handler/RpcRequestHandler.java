package com.lzb.remoting.handler;

import com.lzb.config.RpcConfig;
import com.lzb.enums.FailureStrategyEnum;
import com.lzb.exception.RpcException;
import com.lzb.flowcontrol.FlowControl;
import com.lzb.provider.ServiceProvider;
import com.lzb.remoting.dto.RpcRequest;
import com.lzb.serviceloader.ServiceLoader;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * rpc 方法执行器
 */
@Slf4j
public class RpcRequestHandler {
    private final ServiceProvider serviceProvider;
    private final FlowControl flowControl;
    private final RpcConfig rpcConfig = RpcConfig.getRpcConfig();

    public RpcRequestHandler() {
        serviceProvider = ServiceLoader.getServiceLoader(ServiceProvider.class).getService(rpcConfig.getServiceProvider());
        flowControl = ServiceLoader.getServiceLoader(FlowControl.class).getService(rpcConfig.getFlowControl());
    }

    /**
     * 执行目标方法
     */
    public Object handle(RpcRequest rpcRequest) {
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
