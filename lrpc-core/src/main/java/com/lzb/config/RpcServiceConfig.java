package com.lzb.config;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcServiceConfig {

    /**
     * 服务对象
     */
    private Object service;

    public String getRpcServiceName() {
        return this.service.getClass().getInterfaces()[0].getCanonicalName();
    }

}
