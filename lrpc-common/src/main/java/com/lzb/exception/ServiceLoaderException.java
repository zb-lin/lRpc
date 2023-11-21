package com.lzb.exception;


import com.lzb.enums.ServiceLoaderErrorEnum;

public class ServiceLoaderException extends RuntimeException {
    private static final long serialVersionUID = -8947125319858593890L;

    public ServiceLoaderException(ServiceLoaderErrorEnum serviceLoaderErrorEnum, String detail) {
        super(serviceLoaderErrorEnum.getMessage() + ":" + detail);
    }

    public ServiceLoaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceLoaderException(ServiceLoaderErrorEnum serviceLoaderErrorEnum) {
        super(serviceLoaderErrorEnum.getMessage());
    }
}
