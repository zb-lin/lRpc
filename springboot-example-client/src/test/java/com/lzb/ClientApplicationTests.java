package com.lzb;

import com.lzb.annotation.RpcScan;
import com.lzb.controller.HelloController;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
@RpcScan(basePackage = {"com.lzb"})
class ClientApplicationTests {


    @Resource
    private HelloController helloController;

    @Test
    void contextLoads() {
        helloController.test();
    }

}
