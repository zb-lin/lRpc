package com.lzb.exception;


public class SerializeException extends RuntimeException {
    private static final long serialVersionUID = -6007991529439560599L;

    public SerializeException(String message) {
        super(message);
    }
}
