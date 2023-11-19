package com.lzb;

import com.lzb.config.RpcConfig;
import com.lzb.enums.*;
import com.lzb.utils.StringUtil;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;

@Configuration
@ConfigurationProperties("lrpc.config")
@Data
@ComponentScan
public class LRpcConfig {
    private String rpcClient;
    private String serviceRegistry;
    private String serviceProvider;
    private String serviceDiscovery;
    private String serialization;
    private String compress;
    private String loadBalance;
    private String registryHost;
    private String registryPort;

    @PostConstruct
    public void updateConfiguration() {
        updateConfiguration(rpcClient, RpcClientEnum.class);
        updateConfiguration(serviceRegistry, ServiceRegistryEnum.class);
        updateConfiguration(serviceProvider, ServiceProviderEnum.class);
        updateConfiguration(serviceDiscovery, ServiceDiscoveryEnum.class);
        updateConfiguration(serialization, SerializationEnum.class);
        updateConfiguration(compress, CompressEnum.class);
        updateConfiguration(loadBalance, LoadBalanceEnum.class);
        RpcConfig rpcConfig = RpcConfig.getRpcConfig();
        if (StringUtil.isNotBlank(registryHost)) {
            rpcConfig.setRegistryHost(registryHost);
        }
        if (StringUtil.isNotBlank(registryPort)) {
            rpcConfig.setRegistryHost(registryPort);
        }
    }


    @SneakyThrows
    private void updateConfiguration(String value, Class<?> enumClass) {
        Method method = enumClass.getMethod("contains", String.class);
        Boolean result = (Boolean) method.invoke(null, value);
        if (StringUtil.isNotBlank(value) && result) {
            String methodName = "set" + enumClass.getSimpleName();
            methodName = methodName.substring(0, methodName.length() - 4);
            Method rpcConfigMethod = RpcConfig.class.getMethod(methodName, String.class);
            RpcConfig rpcConfig = RpcConfig.getRpcConfig();
            rpcConfigMethod.invoke(rpcConfig, value);
        }
    }

}
