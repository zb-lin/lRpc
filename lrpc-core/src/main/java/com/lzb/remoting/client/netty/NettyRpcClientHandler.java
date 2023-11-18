package com.lzb.remoting.client.netty;


import com.lzb.enums.CompressTypeEnum;
import com.lzb.enums.RpcMessageTypeEnum;
import com.lzb.enums.SerializationTypeEnum;
import com.lzb.factory.SingletonFactory;
import com.lzb.remoting.client.UnprocessedRequests;
import com.lzb.remoting.constants.RpcConstants;
import com.lzb.remoting.dto.RpcMessage;
import com.lzb.remoting.dto.RpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class NettyRpcClientHandler extends ChannelInboundHandlerAdapter {
    private final UnprocessedRequests unprocessedRequests;
    private final NettyRpcClient nettyRpcClient;

    public NettyRpcClientHandler() {
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
        this.nettyRpcClient = SingletonFactory.getInstance(NettyRpcClient.class);
    }

    /**
     * Read the message transmitted by the server
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            log.info("Client accepts messages: [{}]", msg);
            if (msg instanceof RpcMessage) {
                RpcMessage tmp = (RpcMessage) msg;
                int messageType = tmp.getMessageType();
                // 心跳检测
                if (messageType == RpcMessageTypeEnum.HEARTBEAT_RESPONSE_TYPE.getCode()) {
                    log.info("heart [{}]", tmp.getData());
                } else if (messageType == RpcMessageTypeEnum.RESPONSE_TYPE.getCode()) {
                    RpcResponse<Object> rpcResponse = (RpcResponse<Object>) tmp.getData();
                    unprocessedRequests.complete(rpcResponse);
                }
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    /**
     * 处理 读写空闲时长超过设置的时间范围 的回调
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                log.info("Heartbeat detection timeout: [{}]", ctx.channel().remoteAddress());
                Channel channel = nettyRpcClient.getChannel((InetSocketAddress) ctx.channel().remoteAddress());
                RpcMessage rpcMessage = new RpcMessage();
                rpcMessage.setCodec(SerializationTypeEnum.PROTOSTUFF.getCode());
                rpcMessage.setCompress(CompressTypeEnum.GZIP.getCode());
                rpcMessage.setMessageType(RpcMessageTypeEnum.HEARTBEAT_REQUEST_TYPE.getCode());
                rpcMessage.setData(RpcConstants.PING);
                channel.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Client Exception: ", cause);
        cause.printStackTrace();
        ctx.close();
    }

}

