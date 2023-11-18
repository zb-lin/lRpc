package com.lzb.remoting.dto;

import lombok.*;

import java.io.Serializable;

/**
 * 客户端请求消息
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class RpcRequest implements Serializable {
    private static final long serialVersionUID = 355971735661518415L;
    private String requestId;
    private String interfaceName;
    private String methodName;
    private Object[] parameters;
    private Class<?>[] paramTypes;

    public String getRpcServiceName() {
        return interfaceName;
    }
}
