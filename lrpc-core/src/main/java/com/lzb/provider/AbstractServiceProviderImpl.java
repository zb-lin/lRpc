package com.lzb.provider;

import com.lzb.config.RpcConfig;
import com.lzb.config.RpcServiceConfig;
import com.lzb.enums.RpcErrorMessageEnum;
import com.lzb.exception.RpcException;
import com.lzb.registry.ServiceRegistry;
import com.lzb.serviceloader.ServiceLoader;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.lzb.remoting.constants.RpcConstants.PORT;


@Slf4j
public abstract class AbstractServiceProviderImpl implements ServiceProvider {

    protected final Map<String, Object> serviceMap;
    protected final Set<String> registeredService;
    protected final ServiceRegistry serviceRegistry;

    public AbstractServiceProviderImpl() {
        serviceMap = new ConcurrentHashMap<>();
        registeredService = ConcurrentHashMap.newKeySet();
        serviceRegistry = ServiceLoader.getServiceLoader(ServiceRegistry.class).getService(RpcConfig.getRpcConfig().getServiceRegistry());
    }

    @Override
    public void publishService(RpcServiceConfig rpcServiceConfig) {
        try {
            String host = InetAddress.getLocalHost().getHostAddress();
            this.addService(rpcServiceConfig);
            serviceRegistry.registerService(rpcServiceConfig.getRpcServiceName(), new InetSocketAddress(host, PORT));
        } catch (UnknownHostException e) {
            log.error("the IP address of a host could not be determined.", e);
        }
    }

    @Override
    public void addService(RpcServiceConfig rpcServiceConfig) {
        String rpcServiceName = rpcServiceConfig.getRpcServiceName();
        if (registeredService.contains(rpcServiceName)) {
            return;
        }
        registeredService.add(rpcServiceName);
        serviceMap.put(rpcServiceName, rpcServiceConfig.getService());
        log.info("Add service: {} and interfaces:{}", rpcServiceName, rpcServiceConfig.getService().getClass().getInterfaces());
    }

    @Override
    public Object getService(String rpcServiceName) {
        Object service = serviceMap.get(rpcServiceName);
        if (null == service) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND);
        }
        return service;
    }

    @Override
    public List<String> listServices() {
        return new ArrayList<>(serviceMap.keySet());
    }
}
