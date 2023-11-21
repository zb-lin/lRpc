package com.lzb.transaction.database.config;

import lombok.Data;

@Data
public class RequestContext {
    private ThreadLocal<String> threadLocal;

    private RequestContext() {
        this.threadLocal = new ThreadLocal<>();
    }

    public static RequestContext getInstance() {
        return RequestContextHolder.requestContext;
    }

    /**
     * 静态内部类单例模式
     * 单例初使化
     */
    private static class RequestContextHolder {
        private static final RequestContext requestContext = new RequestContext();
    }

    public String getContext() {
        return threadLocal.get();
    }

    public void setContext(String requestId) {
        threadLocal.set(requestId);
    }

}
