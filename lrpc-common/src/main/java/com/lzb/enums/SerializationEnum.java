package com.lzb.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public enum SerializationEnum {

    KYRO((byte) 0x01, "kyro"),
    PROTOSTUFF((byte) 0x02, "protostuff"),
    HESSIAN((byte) 0X03, "hessian"),
    FTS((byte) 0X04, "fts"),
    PROTOBUF((byte) 0X05, "protobuf");

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
    public static byte getCode(String name) {
        for (SerializationEnum c : SerializationEnum.values()) {
            if (c.getName().equals(name)) {
                return c.code;
            }
        }
        return 0x01;
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
