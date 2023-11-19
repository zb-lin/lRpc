package com.lzb.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CompressEnum {

    GZIP((byte) 0x01, "gzip");

    private final byte code;
    private final String name;

    public static String getName(byte code) {
        for (CompressEnum c : CompressEnum.values()) {
            if (c.getCode() == code) {
                return c.name;
            }
        }
        return null;
    }
    public static Boolean contains(String value) {
        for (CompressEnum v : values()) {
            if (v.getName().equals(value)) {
                return true;
            }
        }
        return false;
    }

}
