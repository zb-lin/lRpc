package com.lzb.registry;


import com.lzb.serviceloader.SPI;

import java.net.InetSocketAddress;

/**
 * 注册中心, 服务注册
 */
@SPI
public interface ServiceRegistry {
    /**
     * 注册服务
     *
     * @param rpcServiceName    rpc 服务名称
     * @param inetSocketAddress 服务名称地址
     */
    void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress);

}
