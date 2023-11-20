package com.lzb.serialize.kyro;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.lzb.exception.SerializeException;
import com.lzb.remoting.dto.RpcRequest;
import com.lzb.remoting.dto.RpcResponse;
import com.lzb.serialize.Serializer;
import com.lzb.serialize.hessian.HessianSerializer;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;


@Slf4j
public class KryoSerializer implements Serializer {


    private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RpcResponse.class);
        kryo.register(RpcRequest.class);
        return kryo;
    });

    @Override
    public byte[] serialize(Object target) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             Output output = new Output(byteArrayOutputStream)) {
            Kryo kryo = kryoThreadLocal.get();
            kryo.writeObject(output, target);
            kryoThreadLocal.remove();
            return output.toBytes();
        } catch (Exception e) {
            throw new SerializeException("Serialization failed");
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> targetClass) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             Input input = new Input(byteArrayInputStream)) {
            Kryo kryo = kryoThreadLocal.get();
            Object result = kryo.readObject(input, targetClass);
            kryoThreadLocal.remove();
            return targetClass.cast(result);
        } catch (Exception e) {
            throw new SerializeException("Deserialization failed");
        }
    }
    public static void main(String[] args) {
        KryoSerializer kryoSerializer = new KryoSerializer();
        RpcRequest request = new RpcRequest("7aa332c7-f0f8-40a7-baa3-efa5baa8cb6a", "com.lzb.HelloService", "hello"
                , new String[]{"111", "222"}, new Class[]{RpcRequest.class});
        byte[] serialize = kryoSerializer.serialize(request);
        System.out.println(Arrays.toString(serialize));
        System.out.println(serialize.length);
        RpcRequest deserialize = kryoSerializer.deserialize(serialize, RpcRequest.class);
        System.out.println(deserialize);
    }
}
