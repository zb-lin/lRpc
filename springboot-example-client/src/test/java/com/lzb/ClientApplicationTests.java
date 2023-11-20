package com.lzb;

import com.lzb.controller.HelloController;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
@EnableLRpc
class ClientApplicationTests {

    @Resource
    private HelloController helloController;

    @Test
    void contextLoads() {
        helloController.test();
    }

}
