package com.lzb.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public enum ServiceProviderEnum {

    ZK("zk"),
    NACOS("nacos");

    private final String name;
}
