package com.lzb.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public enum LoadBalanceEnum {

    LOAD_BALANCE("loadBalance");

    private final String name;
    public static Boolean contains(String value) {
        for (LoadBalanceEnum v : values()) {
            if (v.getName().equals(value)) {
                return true;
            }
        }
        return false;
    }
}
