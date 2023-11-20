package com.lzb.serialize;

import com.lzb.serviceloader.SPI;

/**
 * 序列化接口，所有序列化类都要实现这个接口
 */
@SPI
public interface Serializer {
    /**
     * 序列化
     *
     * @param target 要序列化的对象
     * @return 字节数组
     */
    byte[] serialize(Object target);

    /**
     * 反序列化
     *
     * @param bytes 序列化后的字节数组
     * @param targetClass 目标类
     * @param <T>   类的类型。举个例子,  {@code String.class} 的类型是 {@code Class<String>}.
     *              如果不知道类的类型的话，使用 {@code Class<?>}
     * @return 反序列化的对象
     */
    <T> T deserialize(byte[] bytes, Class<T> targetClass);
}
