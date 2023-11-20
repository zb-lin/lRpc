package com.lzb.loadbalance.random;


import com.lzb.loadbalance.AbstractLoadBalance;
import com.lzb.remoting.dto.RpcRequest;

import java.util.List;
import java.util.Random;

/**
 * 随机
 */
public class RandomLoadBalance extends AbstractLoadBalance {
    @Override
    protected String doSelect(List<String> serviceAddresses, RpcRequest rpcRequest) {
        Random random = new Random();
        return serviceAddresses.get(random.nextInt(serviceAddresses.size()));
    }
}
