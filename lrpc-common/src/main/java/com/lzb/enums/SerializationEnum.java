package com.lzb.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public enum SerializationEnum {

    KYRO((byte) 0x01, "kyro"),
    PROTOSTUFF((byte) 0x02, "protostuff"),
    HESSIAN((byte) 0X03, "hessian");

    private final byte code;
    private final String name;

    public static String getName(byte code) {
        for (SerializationEnum c : SerializationEnum.values()) {
            if (c.getCode() == code) {
                return c.name;
            }
        }
        return null;
    }
    public static Boolean contains(String value) {
        for (SerializationEnum v : values()) {
            if (v.getName().equals(value)) {
                return true;
            }
        }
        return false;
    }
}
