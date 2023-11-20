package com.lzb;

import com.lzb.annotation.RpcScan;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@RpcScan(basePackage = {"com.lzb"})
@Documented
public @interface EnableLRpc {
}
