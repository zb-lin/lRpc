package com.lzb.loadbalance.roundrobin;


import com.lzb.loadbalance.AbstractLoadBalance;
import com.lzb.remoting.dto.RpcRequest;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询
 */
public class RoundRobinLoadBalance extends AbstractLoadBalance {

    private final AtomicInteger atomicInteger = new AtomicInteger(0);

    @Override
    protected String doSelect(List<String> serviceAddresses, RpcRequest rpcRequest) {
        if (atomicInteger.get() >= serviceAddresses.size()) {
            atomicInteger.set(0);
        }
        return serviceAddresses.get(atomicInteger.getAndIncrement());
    }
}
