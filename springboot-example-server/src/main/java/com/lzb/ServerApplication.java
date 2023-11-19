package com.lzb;

import com.lzb.annotation.RpcScan;
import com.lzb.remoting.server.NettyRpcServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@SpringBootApplication
@RpcScan(basePackage = {"com.lzb"})
public class ServerApplication {


    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
        LRpcClient lRpcClient = new LRpcClient();
        lRpcClient.run();
    }
}
