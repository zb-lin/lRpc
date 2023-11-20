package com.lzb.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public enum LoadBalanceEnum {

    CONSISTENT_HASH("consistentHash"),
    P2C("p2c"),
    RANDOM("random"),
    ROUND_ROBIN("roundRobin");

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
