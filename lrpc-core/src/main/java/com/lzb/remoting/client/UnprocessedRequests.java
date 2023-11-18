package com.lzb.remoting.client;


import com.lzb.remoting.dto.RpcResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务器未响应请求集合
 */
public class UnprocessedRequests {
    private static final Map<String, CompletableFuture<RpcResponse<Object>>> UN_RESPONSE_TASK = new ConcurrentHashMap<>();

    public void put(String requestId, CompletableFuture<RpcResponse<Object>> completableFuture) {
        UN_RESPONSE_TASK.put(requestId, completableFuture);
    }

    public void complete(RpcResponse<Object> rpcResponse) {
        CompletableFuture<RpcResponse<Object>> completableFuture = UN_RESPONSE_TASK.remove(rpcResponse.getRequestId());
        if (null != completableFuture) {
            completableFuture.complete(rpcResponse);
        } else {
            throw new IllegalStateException();
        }
    }
}
