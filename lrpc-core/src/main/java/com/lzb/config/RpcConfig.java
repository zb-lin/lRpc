package com.lzb.config;

import com.lzb.enums.*;
import lombok.Data;

@Data
public class RpcConfig {

    private static final class RpcConfigHolder {
        static final RpcConfig rpcConfig = new RpcConfig();
    }

    public static RpcConfig getRpcConfig() {
        return RpcConfigHolder.rpcConfig;
    }

    /**
     * 默认 RpcClient
     */
    private static final String DEFAULT_RPC_CLIENT = RpcClientEnum.NETTY.getName();
    /**
     * 默认 ServiceRegistry
     */
    private static final String DEFAULT_SERVICE_REGISTRY = ServiceRegistryEnum.NACOS.getName();
    /**
     * 默认 ServiceProvider
     */
    private static final String DEFAULT_SERVICE_PROVIDER = ServiceProviderEnum.NACOS.getName();

    /**
     * 默认 ServiceDiscovery
     */
    private static final String DEFAULT_SERVICE_DISCOVERY = ServiceDiscoveryEnum.NACOS.getName();
    /**
     * 默认 Serialization
     */
    private static final String DEFAULT_SERIALIZATION_TYPE = SerializationEnum.HESSIAN.getName();
    /**
     * 默认 Compress
     */
    private static final String DEFAULT_COMPRESS_TYPE = CompressEnum.GZIP.getName();
    /**
     * 默认 LoadBalance
     */
    private static final String DEFAULT_LOAD_BALANCE = LoadBalanceEnum.LOAD_BALANCE.getName();

    private RpcConfig() {
        rpcClient = DEFAULT_RPC_CLIENT;
        serviceRegistry = DEFAULT_SERVICE_REGISTRY;
        serviceProvider = DEFAULT_SERVICE_PROVIDER;
        serviceDiscovery = DEFAULT_SERVICE_DISCOVERY;
        serialization = DEFAULT_SERIALIZATION_TYPE;
        compress = DEFAULT_COMPRESS_TYPE;
        loadBalance = DEFAULT_LOAD_BALANCE;
    }

    private String rpcClient;
    private String serviceRegistry;
    private String serviceProvider;
    private String serviceDiscovery;
    private String serialization;
    private String compress;
    private String loadBalance;
    private String registryHost;
    private String registryPort;
}
