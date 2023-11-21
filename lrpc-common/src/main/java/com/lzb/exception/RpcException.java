package com.lzb.exception;


import com.lzb.enums.RpcErrorMessageEnum;

public class RpcException extends RuntimeException {
    private static final long serialVersionUID = 2807201325826038925L;

    public RpcException(RpcErrorMessageEnum rpcErrorMessageEnum, String detail) {
        super(rpcErrorMessageEnum.getMessage() + ":" + detail);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(RpcErrorMessageEnum rpcErrorMessageEnum) {
        super(rpcErrorMessageEnum.getMessage());
    }

    public RpcException(String message) {
        super(message);
    }
}
