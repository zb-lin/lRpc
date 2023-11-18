package com.lzb.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public enum RpcClientEnum {

    NETTY("netty");

    private final String name;
}
