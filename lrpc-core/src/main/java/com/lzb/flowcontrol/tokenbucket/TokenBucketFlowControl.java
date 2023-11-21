package com.lzb.flowcontrol.tokenbucket;

import com.google.common.util.concurrent.RateLimiter;
import com.lzb.config.RpcConfig;
import com.lzb.flowcontrol.FlowControl;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("all")
@Slf4j
public class TokenBucketFlowControl implements FlowControl {

    public static final Map<String, RateLimiter> rateLimiterMap = new ConcurrentHashMap<>();
    private RpcConfig rpcConfig = RpcConfig.getRpcConfig();

    @Override
    public void doFlowControlWithWait(String key) {
        getRateLimiter(key).acquire();
    }

    @Override
    public boolean doFlowControlWithFailFast(String key) {
        return getRateLimiter(key).tryAcquire();
    }

    private RateLimiter getRateLimiter(String key) {
        RateLimiter rateLimiter = rateLimiterMap.get(key);
        if (rateLimiter == null) {
            rateLimiter = RateLimiter.create(Double.parseDouble(rpcConfig.getQps()));
            log.info("create tokenBucket for [{}]", key);
            rateLimiterMap.putIfAbsent(key, rateLimiter);
        }
        return rateLimiter;
    }

}
