package com.lzb.loadbalance;


import com.lzb.remoting.dto.RpcRequest;
import com.lzb.serviceloader.SPI;

import java.util.List;

/**
 * 负载均衡
 */
@SPI
public interface LoadBalance {

    String selectServiceAddress(List<String> serviceUrlList, RpcRequest rpcRequest);
}
