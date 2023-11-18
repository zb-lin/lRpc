package com.lzb.registry.nacos;


import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzb.enums.RpcErrorMessageEnum;
import com.lzb.exception.RpcException;
import com.lzb.loadbalance.LoadBalance;
import com.lzb.registry.ServiceDiscovery;
import com.lzb.registry.nacos.dto.NacosInetAddress;
import com.lzb.registry.nacos.util.NacosUtils;
import com.lzb.remoting.dto.RpcRequest;
import com.lzb.serviceloader.ServiceLoader;
import com.lzb.threadpool.RpcConfig;
import com.lzb.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 服务发现 基于 nacos
 */
@Slf4j
@Service
public class NacosServiceDiscoveryImpl implements ServiceDiscovery {
    private static final String DEFAULT_GROUP = "DEFAULT_GROUP";
    private final static Gson GSON = new Gson();
    private final LoadBalance loadBalance;

    public NacosServiceDiscoveryImpl() {
        this.loadBalance = ServiceLoader.getServiceLoader(LoadBalance.class).getService(RpcConfig.getRpcConfig().getLoadBalance());
    }

    @Override
    public InetSocketAddress lookupService(RpcRequest rpcRequest) {
        try {
            String rpcServiceName = rpcRequest.getRpcServiceName();
            ConfigService configService = NacosUtils.getConfigService();
            String content = configService.getConfig(rpcServiceName, DEFAULT_GROUP, 5000);
            if (StringUtil.isBlank(content)) {
                throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND, rpcServiceName);
            }
            List<NacosInetAddress> nacosInetAddressList = GSON.fromJson(content, new TypeToken<List<NacosInetAddress>>() {
            }.getType());
            List<String> nacosInetAddressStrList = nacosInetAddressList.stream().map(NacosInetAddress::toString).collect(Collectors.toList());
            String targetServiceUrl = loadBalance.selectServiceAddress(nacosInetAddressStrList, rpcRequest);
            log.info("Discovery Service Success:[{}]", targetServiceUrl);
            targetServiceUrl = targetServiceUrl.substring(targetServiceUrl.indexOf("(") + 1, targetServiceUrl.length() - 1);
            String[] strings = targetServiceUrl.split(",");
            return new InetSocketAddress(strings[0].split("=")[1], Integer.parseInt(strings[1].split("=")[1]));
        } catch (NacosException e) {
            log.error(e.getMessage());
            throw new RpcException("Service Discovery Exception", e);
        }
    }
}

