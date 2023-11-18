package com.lzb.remoting.dto;


import lombok.*;

/**
 * 客户端与服务端交互的消息体
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcMessage {

    /**
     * 消息类型  REQUEST_TYPE = 1  请求   RESPONSE_TYPE = 2  响应
     */
    private byte messageType;
    /**
     * 序列化类型
     */
    private byte codec;
    /**
     * 压缩类型
     */
    private byte compress;
    /**
     * 请求 id
     */
    private int requestId;
    /**
     * 请求 数据
     */
    private Object data;

}
