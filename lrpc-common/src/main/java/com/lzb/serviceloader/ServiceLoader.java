package com.lzb.serviceloader;


import com.lzb.enums.ServiceLoaderErrorEnum;
import com.lzb.exception.RpcException;
import com.lzb.exception.ServiceLoaderException;
import com.lzb.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
public final class ServiceLoader<T> {
    /**
     * 文件前缀
     */
    private static final String PREFIX = "services/";
    /**
     * value 对应 services/ 下一个文件  对应一个接口
     */
    private static final Map<Class<?>, ServiceLoader<?>> SERVICE_LOADERS_MAP = new ConcurrentHashMap<>();
    /**
     * value 对应 services/ 下一个文件的一个接口实现类
     */
    private static final Map<Class<?>, Object> SERVICES_MAP = new ConcurrentHashMap<>();
    private final Holder<Map<String, Class<?>>> loadedServiceClasses = new Holder<>();
    private final Class<?> type;

    private ServiceLoader(Class<?> type) {
        this.type = type;
    }

    public static <S> ServiceLoader<S> getServiceLoader(Class<S> type) {
        if (type == null) {
            throw new ServiceLoaderException(ServiceLoaderErrorEnum.SERVICE_TYPE_IS_NULL);
        }
        if (!type.isInterface()) {
            throw new ServiceLoaderException(ServiceLoaderErrorEnum.SERVICE_TYPE_IS_NOT_INTERFACE);
        }
        if (type.getAnnotation(SPI.class) == null) {
            throw new ServiceLoaderException(ServiceLoaderErrorEnum.SERVICE_TYPE_IS_NOT_ANNOTATED_BY_SPI);
        }
        ServiceLoader<S> serviceLoader = (ServiceLoader<S>) SERVICE_LOADERS_MAP.get(type);
        if (serviceLoader == null) {
            SERVICE_LOADERS_MAP.putIfAbsent(type, new ServiceLoader<S>(type));
            serviceLoader = (ServiceLoader<S>) SERVICE_LOADERS_MAP.get(type);
        }
        return serviceLoader;
    }

    public T getService(String name) {
        if (StringUtil.isBlank(name)) {
            throw new ServiceLoaderException(ServiceLoaderErrorEnum.SERVICE_TYPE_IS_NULL);
        }
        Class<?> targetClass = getServiceClasses().get(name);
        if (targetClass == null) {
            throw new RpcException("The service does not exist: " + name);
        }
        T service = (T) SERVICES_MAP.get(targetClass);
        if (service == null) {
            try {
                SERVICES_MAP.putIfAbsent(targetClass, targetClass.newInstance());
                service = (T) SERVICES_MAP.get(targetClass);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return service;
    }

    private Map<String, Class<?>> getServiceClasses() {
        Map<String, Class<?>> serviceClasses = loadedServiceClasses.get();
        if (serviceClasses == null) {
            synchronized (loadedServiceClasses) {
                serviceClasses = loadedServiceClasses.get();
                if (serviceClasses == null) {
                    serviceClasses = new HashMap<>();
                    loadServices(serviceClasses);
                    loadedServiceClasses.set(serviceClasses);
                }
            }
        }
        return serviceClasses;
    }

    /**
     * 读取所有文件
     */
    private void loadServices(Map<String, Class<?>> serviceClasses) {
        String fileName = ServiceLoader.PREFIX + type.getName();
        try {
            Enumeration<URL> urls;
            ClassLoader classLoader = ServiceLoader.class.getClassLoader();
            urls = classLoader.getResources(fileName);
            if (urls != null) {
                while (urls.hasMoreElements()) {
                    URL resourceUrl = urls.nextElement();
                    loadServices(serviceClasses, classLoader, resourceUrl);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 读取文件
     */
    private void loadServices(Map<String, Class<?>> serviceClasses, ClassLoader classLoader, URL resourceUrl) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceUrl.openStream(), UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // # 后为注释
                final int ci = line.indexOf('#');
                if (ci >= 0) {
                    line = line.substring(0, ci);
                }
                line = line.trim();
                if (line.length() > 0) {
                    try {
                        final int ei = line.indexOf('=');
                        String name = line.substring(0, ei).trim();
                        String className = line.substring(ei + 1).trim();
                        if (StringUtil.isNotBlank(name, className)) {
                            Class<?> serviceClass = classLoader.loadClass(className);
                            serviceClasses.put(name, serviceClass);
                        }
                    } catch (ClassNotFoundException e) {
                        log.error(e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
