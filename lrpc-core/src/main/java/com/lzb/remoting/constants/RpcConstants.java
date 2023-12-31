package com.lzb.remoting.constants;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


public class RpcConstants {

    public static final byte[] MAGIC_NUMBER = {(byte) 'l', (byte) 'r', (byte) 'p', (byte) 'c'};
    public static final byte VERSION = 1;
    public static final byte TOTAL_LENGTH = 16;
    public static final int HEAD_LENGTH = 16;
    public static final String PING = "ping";
    public static final String PONG = "pong";
    public static final int MAX_FRAME_LENGTH = 8 * 1024 * 1024;
    public static final int PORT = 9998;

}
