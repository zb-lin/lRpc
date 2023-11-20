package com.lzb.serialize.protostuff;

import com.lzb.remoting.dto.RpcRequest;
import com.lzb.serialize.Serializer;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProtostuffSerializer implements Serializer {


    private static final LinkedBuffer BUFFER = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
    private static final Map<Class<?>, Schema<?>> schemaCache = new ConcurrentHashMap<>();

    @Override
    public byte[] serialize(Object target) {
        Class<?> targetClass = target.getClass();
        Schema schema = getSchema(targetClass);
        byte[] bytes;
        try {
            bytes = ProtostuffIOUtil.toByteArray(target, schema, BUFFER);
        } finally {
            BUFFER.clear();
        }
        return bytes;
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> targetClass) {
        Schema<T> schema = getSchema(targetClass);
        T obj = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(bytes, obj, schema);
        return obj;
    }

    @SuppressWarnings("unchecked")
    private static <T> Schema<T> getSchema(Class<T> targetClass) {
        Schema<T> schema = (Schema<T>) schemaCache.get(targetClass);
        if (schema == null) {
            schema = RuntimeSchema.getSchema(targetClass);
            if (schema != null) {
                schemaCache.put(targetClass, schema);
            }
        }
        return schema;
    }

    public static void main(String[] args) {
        ProtostuffSerializer protostuffSerializer = new ProtostuffSerializer();
        RpcRequest request = new RpcRequest("7aa332c7-f0f8-40a7-baa3-efa5baa8cb6a", "com.lzb.HelloService", "hello"
                , new String[]{"111", "222"}, new Class[]{RpcRequest.class});
        byte[] serialize = protostuffSerializer.serialize(request);
        System.out.println(serialize.length);
        RpcRequest deserialize = protostuffSerializer.deserialize(serialize, RpcRequest.class);
        System.out.println(deserialize);
    }
}
