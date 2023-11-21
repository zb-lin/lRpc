package com.lzb.remoting.codec;


import com.lzb.compress.Compress;
import com.lzb.config.RpcConfig;
import com.lzb.enums.CompressEnum;
import com.lzb.enums.RpcMessageTypeEnum;
import com.lzb.enums.SerializationEnum;
import com.lzb.remoting.constants.RpcConstants;
import com.lzb.remoting.dto.RpcMessage;
import com.lzb.serialize.Serializer;
import com.lzb.serviceloader.ServiceLoader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * 编码器
 */
@Slf4j
public class RpcMessageEncoder extends MessageToByteEncoder<RpcMessage> {
    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessage rpcMessage, ByteBuf out) {
        try {
            out.writeBytes(RpcConstants.MAGIC_NUMBER);
            out.writeByte(RpcConstants.VERSION);
            out.writerIndex(out.writerIndex() + 4);
            byte messageType = rpcMessage.getMessageType();
            out.writeByte(messageType);
            out.writeByte(rpcMessage.getCodec());
            out.writeByte(rpcMessage.getCompress());
            out.writeInt(ATOMIC_INTEGER.getAndIncrement());
            byte[] bodyBytes = null;
            int fullLength = RpcConstants.HEAD_LENGTH;
            if (messageType != RpcMessageTypeEnum.HEARTBEAT_REQUEST_TYPE.getCode()
                    && messageType != RpcMessageTypeEnum.HEARTBEAT_RESPONSE_TYPE.getCode()) {
                String codecName = SerializationEnum.getName(rpcMessage.getCodec());
                log.info("serialization name: [{}] ", codecName);
                Serializer serializer = ServiceLoader.getServiceLoader(Serializer.class)
                        .getService(codecName);
                bodyBytes = serializer.serialize(rpcMessage.getData());
                String compressName = CompressEnum.getName(rpcMessage.getCompress());
                log.info("compress name: [{}] ", compressName);
                Compress compress = ServiceLoader.getServiceLoader(Compress.class)
                        .getService(compressName);
                bodyBytes = compress.compress(bodyBytes);
                fullLength += bodyBytes.length;
            }
            if (bodyBytes != null) {
                out.writeBytes(bodyBytes);
            }
            int writeIndex = out.writerIndex();
            out.writerIndex(writeIndex - fullLength + RpcConstants.MAGIC_NUMBER.length + 1);
            out.writeInt(fullLength);
            out.writerIndex(writeIndex);
        } catch (Exception e) {
            log.error("Encode request error!", e);
        }

    }


}

