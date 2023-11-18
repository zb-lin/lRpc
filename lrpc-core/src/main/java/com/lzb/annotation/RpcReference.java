package com.lzb.annotation;


import java.lang.annotation.*;


@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
// 可以被子类继承
@Inherited
public @interface RpcReference {
}
