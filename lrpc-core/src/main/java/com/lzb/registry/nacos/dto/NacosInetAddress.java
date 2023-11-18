package com.lzb.registry.nacos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.net.InetSocketAddress;

@Data
@AllArgsConstructor
public class NacosInetAddress {
    public String host;
    public Integer port;

    public NacosInetAddress(InetSocketAddress inetSocketAddress) {
        this.host = inetSocketAddress.getHostString();
        this.port = inetSocketAddress.getPort();
    }


}
