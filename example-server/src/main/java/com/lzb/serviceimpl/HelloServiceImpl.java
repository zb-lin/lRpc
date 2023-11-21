package com.lzb.serviceimpl;

import com.lzb.Hello;
import com.lzb.HelloService;
import com.lzb.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RpcService
public class HelloServiceImpl implements HelloService {

    @Override
    public String hello(Hello hello) {
        log.info("HelloServiceImpl收到: {}.", hello.getMessage());
        String result = "Hello description is " + hello.getDescription();
        log.info("HelloServiceImpl返回: {}.", result);
        return result;
    }
}
