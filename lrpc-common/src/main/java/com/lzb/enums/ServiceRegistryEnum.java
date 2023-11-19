package com.lzb.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public enum ServiceRegistryEnum {

    ZK("zk"),
    NACOS("nacos");

    private final String name;
    public static Boolean contains(String value) {
        for (ServiceRegistryEnum v : values()) {
            if (v.getName().equals(value)) {
                return true;
            }
        }
        return false;
    }
}
