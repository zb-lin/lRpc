package com.lzb.registry.nacos;

import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzb.registry.ServiceRegistry;
import com.lzb.registry.nacos.dto.NacosInetAddress;
import com.lzb.registry.nacos.util.NacosUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;

/**
 * 基于 nacos 的注册中心
 */
@Slf4j
public class NacosServiceRegistryImpl implements ServiceRegistry {
    private static final String DEFAULT_GROUP = "DEFAULT_GROUP";
    private final static Gson GSON = new Gson();

    @Override
    public void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        try {
            ConfigService configService = NacosUtils.getConfigService();
            String content = configService.getConfig(rpcServiceName, DEFAULT_GROUP, 5000);
            boolean isPublishOk;
            if (content == null) {
                isPublishOk = configService.publishConfig(rpcServiceName, DEFAULT_GROUP,
                        GSON.toJson(Collections.singletonList(new NacosInetAddress(inetSocketAddress))));
            } else {
                List<NacosInetAddress> nacosInetAddressList = GSON.fromJson(content, new TypeToken<List<NacosInetAddress>>() {
                }.getType());
                nacosInetAddressList.add(new NacosInetAddress(inetSocketAddress));
                isPublishOk = configService.publishConfig(rpcServiceName, DEFAULT_GROUP,
                        GSON.toJson(nacosInetAddressList));
            }
            log.info("[{}]Service Registry: {}", rpcServiceName, isPublishOk);
        } catch (NacosException e) {
            log.error("Service registration failed: [{}] [{}] [{}]", rpcServiceName, inetSocketAddress.toString(), e.getMessage());
        }
    }
}
