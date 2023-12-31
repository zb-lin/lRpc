package com.lzb.serialize.hessian;


import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.lzb.exception.SerializeException;
import com.lzb.remoting.dto.RpcRequest;
import com.lzb.serialize.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
public class HessianSerializer implements Serializer {
    @Override
    public byte[] serialize(Object target) {
        Hessian2Output hessian2Output = null;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            hessian2Output = new Hessian2Output(byteArrayOutputStream);
            hessian2Output.writeObject(target);
            hessian2Output.flush();
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw new SerializeException("Hessian Serialization failed");
        } finally {
            try {
                if (hessian2Output != null) {
                    hessian2Output.close();
                }
            } catch (IOException e) {
                log.error("hessian2Output close failed");
            }
        }

    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> targetClass) {
        Hessian2Input hessian2Input = null;
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)) {
            hessian2Input = new Hessian2Input(byteArrayInputStream);
            Object result = hessian2Input.readObject();
            return targetClass.cast(result);
        } catch (Exception e) {
            throw new SerializeException("Hessian Deserialization failed");
        } finally {
            try {
                if (hessian2Input != null) {
                    hessian2Input.close();
                }
            } catch (IOException e) {
                log.error("hessian2Input close failed");
            }
        }
    }

    public static void main(String[] args) {
        HessianSerializer hessianSerializer = new HessianSerializer();
        RpcRequest request = new RpcRequest("7aa332c7-f0f8-40a7-baa3-efa5baa8cb6a", "com.lzb.HelloService", "hello"
                , new String[]{"111", "222"}, new Class[]{RpcRequest.class});
        byte[] serialize = hessianSerializer.serialize(request);
        System.out.println(serialize.length);
        RpcRequest deserialize = hessianSerializer.deserialize(serialize, RpcRequest.class);
        System.out.println(deserialize);
    }

}
