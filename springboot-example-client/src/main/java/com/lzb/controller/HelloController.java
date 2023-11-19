package com.lzb.controller;

import com.lzb.Hello;
import com.lzb.HelloService;
import com.lzb.annotation.RpcReference;
import org.springframework.stereotype.Component;

/**
 * @author smile2coder
 */
@Component
public class HelloController {

    @RpcReference
    private HelloService helloService;

    public void test() {
        String hello = this.helloService.hello(new Hello("111", "222"));

        System.out.println(hello);
    }
}
