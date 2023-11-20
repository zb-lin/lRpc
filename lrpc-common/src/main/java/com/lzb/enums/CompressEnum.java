package com.lzb.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CompressEnum {

    BZIP2((byte) 0x01, "bzip2"),
    DEFLATE((byte) 0x02, "deflate"),
    GZIP((byte) 0x03, "gzip"),
    LZO((byte) 0x04, "lzo"),
    SNAPPY((byte) 0x05, "snappy");

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

    public static byte getCode(String name) {
        for (CompressEnum c : CompressEnum.values()) {
            if (c.getName().equals(name)) {
                return c.code;
            }
        }
        return 0x05;
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
