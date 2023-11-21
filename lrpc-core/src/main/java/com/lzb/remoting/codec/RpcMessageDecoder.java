package com.lzb.remoting.codec;

import com.lzb.compress.Compress;
import com.lzb.enums.CompressEnum;
import com.lzb.enums.RpcMessageTypeEnum;
import com.lzb.enums.SerializationEnum;
import com.lzb.remoting.constants.RpcConstants;
import com.lzb.remoting.dto.RpcMessage;
import com.lzb.remoting.dto.RpcRequest;
import com.lzb.remoting.dto.RpcResponse;
import com.lzb.serialize.Serializer;
import com.lzb.serviceloader.ServiceLoader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * 解码器
 */
@Slf4j
public class RpcMessageDecoder extends LengthFieldBasedFrameDecoder {
    public RpcMessageDecoder() {
        // lengthFieldOffset: magic code is 4B, and version is 1B, and then full length. so value is 5
        // lengthFieldLength: full length is 4B. so value is 4
        // lengthAdjustment: full length include all data and read 9 bytes before, so the left length is (fullLength-9). so values is -9
        // initialBytesToStrip: we will check magic code and version manually, so do not strip any bytes. so values is 0
        this(RpcConstants.MAX_FRAME_LENGTH, 5, 4, -9, 0);
    }

    /**
     * @param maxFrameLength      最大帧长度。它决定可以接收的数据的最大长度。如果超过，数据将被丢弃。
     * @param lengthFieldOffset   长度字段偏移。长度字段是跳过指定字节长度的字段。
     * @param lengthFieldLength   长度字段中的字节数。
     * @param lengthAdjustment    要添加到长度字段值的补偿值
     * @param initialBytesToStrip 跳过的字节数。如果您需要接收所有标头+正文数据，如果您只想接收正文数据，则此值为0，则需要跳过标头消耗的字节数。
     */
    public RpcMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength,
                             int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decoded = super.decode(ctx, in);
        if (decoded instanceof ByteBuf) {
            ByteBuf frame = (ByteBuf) decoded;
            if (frame.readableBytes() >= RpcConstants.TOTAL_LENGTH) {
                try {
                    return decodeFrame(frame);
                } catch (Exception e) {
                    log.error("Decode frame error!", e);
                    throw e;
                } finally {
                    frame.release();
                }
            }
        }
        return decoded;
    }


    private Object decodeFrame(ByteBuf in) {
        checkMagicNumber(in);
        checkVersion(in);
        int fullLength = in.readInt();
        byte messageType = in.readByte();
        byte codecType = in.readByte();
        byte compressType = in.readByte();
        int requestId = in.readInt();
        RpcMessage rpcMessage = RpcMessage.builder()
                .codec(codecType)
                .requestId(requestId)
                .messageType(messageType).build();
        if (messageType == RpcMessageTypeEnum.HEARTBEAT_REQUEST_TYPE.getCode()) {
            rpcMessage.setData(RpcConstants.PING);
            return rpcMessage;
        }
        if (messageType == RpcMessageTypeEnum.HEARTBEAT_RESPONSE_TYPE.getCode()) {
            rpcMessage.setData(RpcConstants.PONG);
            return rpcMessage;
        }
        int bodyLength = fullLength - RpcConstants.HEAD_LENGTH;
        if (bodyLength > 0) {
            byte[] bs = new byte[bodyLength];
            in.readBytes(bs);
            String compressName = CompressEnum.getName(compressType);
            Compress compress = ServiceLoader.getServiceLoader(Compress.class)
                    .getService(compressName);
            log.info("compress name: [{}] ", compressName);
            bs = compress.decompress(bs);
            String codecName = SerializationEnum.getName(rpcMessage.getCodec());
            log.info("serialization name: [{}] ", codecName);
            Serializer serializer = ServiceLoader.getServiceLoader(Serializer.class)
                    .getService(codecName);
            if (messageType == RpcMessageTypeEnum.REQUEST_TYPE.getCode()) {
                RpcRequest tmpValue = serializer.deserialize(bs, RpcRequest.class);
                rpcMessage.setData(tmpValue);
            } else {
                RpcResponse tmpValue = serializer.deserialize(bs, RpcResponse.class);
                rpcMessage.setData(tmpValue);
            }
        }
        return rpcMessage;
    }

    private void checkVersion(ByteBuf in) {
        // read the version and compare
        byte version = in.readByte();
        if (version != RpcConstants.VERSION) {
            throw new RuntimeException("version isn't compatible" + version);
        }
    }

    private void checkMagicNumber(ByteBuf in) {
        // read the first 4 bit, which is the magic number, and compare
        int len = RpcConstants.MAGIC_NUMBER.length;
        byte[] tmp = new byte[len];
        in.readBytes(tmp);
        for (int i = 0; i < len; i++) {
            if (tmp[i] != RpcConstants.MAGIC_NUMBER[i]) {
                throw new IllegalArgumentException("Unknown magic code: " + Arrays.toString(tmp));
            }
        }
    }
}
