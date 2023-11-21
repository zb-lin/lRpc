package com.lzb.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FlowControlEnum {
    TOKEN_BUCKET("tokenBucket"),
    SENTINEL("sentinel");

    private final String name;

    public static Boolean contains(String value) {
        for (FlowControlEnum v : values()) {
            if (v.getName().equals(value)) {
                return true;
            }
        }
        return false;
    }
}
