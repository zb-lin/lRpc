package com.lzb.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public enum RpcClientEnum {

    NETTY("netty");

    private final String name;

    public static Boolean contains(String value) {
        for (RpcClientEnum v : values()) {
            if (v.getName().equals(value)) {
                return true;
            }
        }
        return false;
    }
}
