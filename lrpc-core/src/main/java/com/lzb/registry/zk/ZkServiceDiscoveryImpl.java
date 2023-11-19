package com.lzb.registry.zk;


import com.lzb.enums.RpcErrorMessageEnum;
import com.lzb.exception.RpcException;
import com.lzb.serviceloader.ServiceLoader;
import com.lzb.loadbalance.LoadBalance;
import com.lzb.registry.ServiceDiscovery;
import com.lzb.registry.zk.util.CuratorUtils;
import com.lzb.remoting.dto.RpcRequest;
import com.lzb.config.RpcConfig;
import com.lzb.utils.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * 服务发现 基于 zk
 *
 */
@Slf4j
@Service
public class ZkServiceDiscoveryImpl implements ServiceDiscovery {
    private final LoadBalance loadBalance;

    public ZkServiceDiscoveryImpl() {
        this.loadBalance = ServiceLoader.getServiceLoader(LoadBalance.class).getService(RpcConfig.getRpcConfig().getLoadBalance());
    }

    @Override
    public InetSocketAddress lookupService(RpcRequest rpcRequest) {
        String rpcServiceName = rpcRequest.getRpcServiceName();
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        List<String> serviceUrlList = CuratorUtils.getChildrenNodes(zkClient, rpcServiceName);
        if (CollectionUtil.isEmpty(serviceUrlList)) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND, rpcServiceName);
        }
        String targetServiceUrl = loadBalance.selectServiceAddress(serviceUrlList, rpcRequest);
        log.info("Successfully found the service address:[{}]", targetServiceUrl);
        String[] socketAddressArray = targetServiceUrl.split(":");
        String host = socketAddressArray[0];
        int port = Integer.parseInt(socketAddressArray[1]);
        return new InetSocketAddress(host, port);
    }
}
