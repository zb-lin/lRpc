package com.lzb;


import com.lzb.annotation.RpcScan;
import com.lzb.config.RpcServiceConfig;
import com.lzb.remoting.server.NettyRpcServer;
import com.lzb.serviceimpl.HelloServiceImpl2;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


@RpcScan(basePackage = {"com.lzb"})
public class NettyServerMain {
    public static void main(String[] args) {
        // Register service via annotation
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(NettyServerMain.class);
        NettyRpcServer nettyRpcServer = (NettyRpcServer) applicationContext.getBean("nettyRpcServer");
        // Register service manually
//        HelloService helloService2 = new HelloServiceImpl2();
//        RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder().service(helloService2).build();
//        nettyRpcServer.registerService(rpcServiceConfig);
        nettyRpcServer.start();
    }
}
