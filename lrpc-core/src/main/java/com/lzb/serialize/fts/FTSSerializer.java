package com.lzb.serialize.fts;


import com.lzb.exception.SerializeException;
import com.lzb.remoting.dto.RpcRequest;
import com.lzb.serialize.Serializer;
import lombok.extern.slf4j.Slf4j;
import org.nustaq.serialization.FSTConfiguration;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
public class FTSSerializer implements Serializer {

    private final ThreadLocal<FSTConfiguration> FSTConfigurationThreadLocal = ThreadLocal.withInitial(FSTConfiguration::createDefaultConfiguration);



    @Override
    public byte[] serialize(Object target) {
        FSTObjectOutput fstObjectOutput = null;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            fstObjectOutput = FSTConfigurationThreadLocal.get().getObjectOutput(byteArrayOutputStream);
            fstObjectOutput.writeObject(target);
            fstObjectOutput.flush();
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw new SerializeException("Hessian Serialization failed");
        } finally {
            try {
                if (fstObjectOutput != null) {
                    fstObjectOutput.close();
                }
            } catch (IOException e) {
                log.error("fstObjectOutput close failed");
            }
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> targetClass) {
        FSTObjectInput fstObjectInput = null;
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)) {
            fstObjectInput = FSTConfigurationThreadLocal.get().getObjectInput(byteArrayInputStream);
            return targetClass.cast(fstObjectInput.readObject(targetClass));
        } catch (Exception e) {
            throw new SerializeException("Hessian Deserialization failed");
        } finally {
            try {
                if (fstObjectInput != null) {
                    fstObjectInput.close();
                }
            } catch (IOException e) {
                log.error("fstObjectInput close failed");
            }
        }
    }

    public static void main(String[] args) {
        FTSSerializer ftsSerializer = new FTSSerializer();
        RpcRequest request = new RpcRequest("7aa332c7-f0f8-40a7-baa3-efa5baa8cb6a", "com.lzb.HelloService", "hello"
                , new String[]{"111", "222"}, new Class[]{RpcRequest.class});
        byte[] serialize = ftsSerializer.serialize(request);
        System.out.println(serialize.length);
        RpcRequest deserialize = ftsSerializer.deserialize(serialize, RpcRequest.class);
        System.out.println(deserialize);
    }

}
