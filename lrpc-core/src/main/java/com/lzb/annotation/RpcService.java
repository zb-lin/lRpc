package com.lzb.annotation;


import java.lang.annotation.*;


@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
// 可以被子类继承
@Inherited
public @interface RpcService {
}
