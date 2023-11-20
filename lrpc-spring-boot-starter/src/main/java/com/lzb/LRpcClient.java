package com.lzb;

import com.lzb.remoting.server.NettyRpcServer;
import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;


@Data
public class LRpcClient {
    private final NettyRpcServer nettyRpcServer = new NettyRpcServer();
    private AtomicInteger atomicInteger = new AtomicInteger(0);


    public void run() {
        if (atomicInteger.compareAndSet(0, 1)) {
            new Thread(nettyRpcServer::start, "netty-rpc-server").start();
        }
    }
}
