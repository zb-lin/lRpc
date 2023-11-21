package com.lzb.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FailureStrategyEnum {

    WAIT("wait"),
    FAIL_FAST("failFast");

    private final String name;
    public static Boolean contains(String value) {
        for (FailureStrategyEnum v : values()) {
            if (v.getName().equals(value)) {
                return true;
            }
        }
        return false;
    }
}
