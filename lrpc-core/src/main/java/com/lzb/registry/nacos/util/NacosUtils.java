package com.lzb.registry.nacos.util;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzb.config.RpcConfig;
import com.lzb.enums.ServiceProviderEnum;
import com.lzb.exception.RpcException;
import com.lzb.provider.ServiceProvider;
import com.lzb.registry.nacos.dto.NacosInetAddress;
import com.lzb.serviceloader.ServiceLoader;
import com.lzb.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static com.alibaba.nacos.api.common.Constants.DEFAULT_GROUP;

@Slf4j
public class NacosUtils {
    private static final RpcConfig rpcConfig = RpcConfig.getRpcConfig();
    private static ConfigService configService;
    private static final ServiceProvider serviceProvider = ServiceLoader.getServiceLoader(ServiceProvider.class).getService(ServiceProviderEnum.NACOS.getName());
    private final static Gson GSON = new Gson();


    public static ConfigService getConfigService() {
        if (configService != null) {
            return configService;
        }
        try {
            // 初始化配置服务，控制台通过示例代码自动获取下面参数
            Properties properties = new Properties();
            String registryHost = rpcConfig.getRegistryHost();
            if (StringUtil.isBlank(registryHost)) {
                throw new RpcException("registryHost is null");
            }
            properties.put("serverAddr", registryHost);
            configService = NacosFactory.createConfigService(properties);
            return configService;
        } catch (NacosException e) {
            throw new RpcException("Failed to initialize configuration service", e);
        }
    }

    public static void clearRegistry(InetSocketAddress inetSocketAddress) {
        List<String> rpcServiceNames = serviceProvider.listServices();
        rpcServiceNames.forEach(rpcServiceName -> {
            try {
                ConfigService configService = NacosUtils.getConfigService();
                String content = configService.getConfig(rpcServiceName, DEFAULT_GROUP, 5000);
                if (content != null) {
                    List<NacosInetAddress> nacosInetAddressList = GSON.fromJson(content, new TypeToken<List<NacosInetAddress>>() {
                    }.getType());
                    nacosInetAddressList = nacosInetAddressList.stream().filter(nacosInetAddress ->
                            !(inetSocketAddress.getHostString().equals(nacosInetAddress.getHost())
                                    && nacosInetAddress.getPort() == inetSocketAddress.getPort())).collect(Collectors.toList());
                    boolean result;
                    if (!nacosInetAddressList.isEmpty()) {
                        result = configService.publishConfig(rpcServiceName, DEFAULT_GROUP,
                                GSON.toJson(nacosInetAddressList));
                    } else {
                        result = configService.removeConfig(rpcServiceName, DEFAULT_GROUP);
                    }
                    log.info("clear Registry [{}]: [{}] [{}]", result, rpcServiceName, inetSocketAddress.toString());
                }
            } catch (NacosException e) {
                log.error("clear Registry failed: [{}] [{}]", rpcServiceName, inetSocketAddress.toString(), e);
            }
        });
    }
}
