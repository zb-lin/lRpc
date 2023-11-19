package com.lzb;

import com.lzb.remoting.server.NettyRpcServer;
import lombok.Data;
import org.springframework.context.annotation.ComponentScan;


@Data
@ComponentScan
public class LRpcClient {
    private final NettyRpcServer nettyRpcServer = new NettyRpcServer();

    public void run() {
        new Thread(nettyRpcServer::start, "netty-rpc-server").start();
    }
}
