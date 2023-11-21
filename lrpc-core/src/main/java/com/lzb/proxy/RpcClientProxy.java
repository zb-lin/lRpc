package com.lzb.proxy;


import com.lzb.enums.RpcErrorMessageEnum;
import com.lzb.enums.RpcResponseCodeEnum;
import com.lzb.exception.RpcException;
import com.lzb.remoting.client.RpcClient;
import com.lzb.remoting.client.netty.NettyRpcClient;
import com.lzb.remoting.dto.RpcRequest;
import com.lzb.remoting.dto.RpcResponse;
import com.lzb.transaction.database.config.RequestContext;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * 通过动态代理发送请求获得结果
 */
@Slf4j
public class RpcClientProxy implements MethodInterceptor {

    private static final String INTERFACE_NAME = "interfaceName";

    /**
     * 执行发送请求的类
     */
    private final RpcClient rpcClient;


    public RpcClientProxy(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
    }


    public <T> T getProxy(Class<T> targetClass) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(targetClass);
        enhancer.setCallback(this);
        return targetClass.cast(enhancer.create());
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        log.info("执行方法: [{}]", method.getName());
        RequestContext requestContext = RequestContext.getInstance();
        RpcRequest rpcRequest = RpcRequest.builder()
                .requestId(requestContext.getContext())
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameters(objects)
                .paramTypes(method.getParameterTypes())
                .build();
        RpcResponse<Object> rpcResponse = null;
        if (this.rpcClient instanceof NettyRpcClient) {
            CompletableFuture<RpcResponse<Object>> completableFuture
                    = (CompletableFuture<RpcResponse<Object>>) this.rpcClient.sendRpcRequest(rpcRequest);
            rpcResponse = completableFuture.get();
        }
        this.check(rpcResponse, rpcRequest);
        return rpcResponse.getData();
    }

    private void check(RpcResponse<Object> rpcResponse, RpcRequest rpcRequest) {
        if (rpcResponse == null) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }

        if (!rpcRequest.getRequestId().equals(rpcResponse.getRequestId())) {
            throw new RpcException(RpcErrorMessageEnum.REQUEST_NOT_MATCH_RESPONSE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }

        if (rpcResponse.getCode() == null || !rpcResponse.getCode().equals(RpcResponseCodeEnum.SUCCESS.getCode())) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }
    }


}
