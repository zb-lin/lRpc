package com.lzb.remoting.client;


import com.lzb.remoting.dto.RpcRequest;
import com.lzb.serviceloader.SPI;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;

/**
 * 发送请求接口
 */
@SPI
public interface RpcClient {
    /**
     * 发送rpc请求并获取结果
     */
    Object sendRpcRequest(RpcRequest rpcRequest);
}
