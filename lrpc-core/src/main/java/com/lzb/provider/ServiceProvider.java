package com.lzb.provider;


import com.lzb.config.RpcServiceConfig;
import com.lzb.serviceloader.SPI;

import java.util.List;

/**
 * 缓存和提供服务
 */
@SPI
public interface ServiceProvider {


    void addService(RpcServiceConfig rpcServiceConfig);


    Object getService(String rpcServiceName);

    List<String> listServices();

    void publishService(RpcServiceConfig rpcServiceConfig);

}
