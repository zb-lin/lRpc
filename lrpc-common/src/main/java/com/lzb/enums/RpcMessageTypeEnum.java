package com.lzb.enums;

public enum RpcMessageTypeEnum {
    REQUEST_TYPE((byte) 1),
    RESPONSE_TYPE((byte) 2),
    HEARTBEAT_REQUEST_TYPE((byte) 3),
    HEARTBEAT_RESPONSE_TYPE((byte) 4);
    private final byte code;

    RpcMessageTypeEnum(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return code;
    }

}
