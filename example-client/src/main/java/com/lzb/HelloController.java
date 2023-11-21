package com.lzb;

import com.lzb.annotation.RpcReference;
import org.springframework.stereotype.Component;


@Component
public class HelloController {

    @RpcReference
    private HelloService helloService;

    public void test() throws InterruptedException {
        String hello = helloService.hello(new Hello("111", "222"));
        System.out.println(hello);
    }
}
