package com.lzb.factory;

import com.lzb.exception.RpcException;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 注册式单例
 */
public final class SingletonFactory {
    private static final Map<String, Object> SINGLETON_OBJECT_MAP = new ConcurrentHashMap<>();

    private SingletonFactory() {
    }

    public static <T> T getInstance(Class<T> targetClass) {
        if (targetClass == null) {
            throw new IllegalArgumentException();
        }
        String key = targetClass.toString();
        if (SINGLETON_OBJECT_MAP.containsKey(key)) {
            return targetClass.cast(SINGLETON_OBJECT_MAP.get(key));
        } else {
            return targetClass.cast(SINGLETON_OBJECT_MAP.computeIfAbsent(key, k -> {
                try {
                    return targetClass.getDeclaredConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException e) {
                    throw new RpcException(key + " " + e.getMessage(), e);
                }
            }));
        }
    }
}
