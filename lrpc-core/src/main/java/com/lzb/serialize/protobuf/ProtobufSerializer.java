package com.lzb.serialize.protobuf;


import com.lzb.serialize.Serializer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProtobufSerializer implements Serializer {
    @Override
    public byte[] serialize(Object target) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> targetClass) {
        throw new UnsupportedOperationException();
    }


}
