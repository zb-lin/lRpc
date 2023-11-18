package com.lzb.annotation;

import com.lzb.spring.BeanRegister;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;


@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Import(BeanRegister.class)
@Documented
public @interface RpcScan {

    String[] basePackage();

}
