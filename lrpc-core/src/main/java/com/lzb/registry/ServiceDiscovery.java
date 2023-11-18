package com.lzb.registry;


import com.lzb.serviceloader.SPI;
import com.lzb.remoting.dto.RpcRequest;

import java.net.InetSocketAddress;

/**
 * 服务发现
 */
@SPI
public interface ServiceDiscovery {
    /**
     * 通过服务名称查找服务
     */
    InetSocketAddress lookupService(RpcRequest rpcRequest);
}
