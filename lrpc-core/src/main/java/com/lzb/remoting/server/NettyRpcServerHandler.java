package com.lzb.remoting.server;

import com.lzb.config.RpcConfig;
import com.lzb.enums.CompressEnum;
import com.lzb.enums.RpcMessageTypeEnum;
import com.lzb.enums.RpcResponseCodeEnum;
import com.lzb.enums.SerializationEnum;
import com.lzb.factory.SingletonFactory;
import com.lzb.remoting.constants.RpcConstants;
import com.lzb.remoting.dto.RpcMessage;
import com.lzb.remoting.dto.RpcRequest;
import com.lzb.remoting.dto.RpcResponse;
import com.lzb.remoting.handler.RpcRequestHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class NettyRpcServerHandler extends ChannelInboundHandlerAdapter {

    private final RpcRequestHandler rpcRequestHandler;

    public NettyRpcServerHandler() {
        this.rpcRequestHandler = SingletonFactory.getInstance(RpcRequestHandler.class);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            if (msg instanceof RpcMessage) {
                log.info("Server receives messages: [{}] ", msg);
                int messageType = ((RpcMessage) msg).getMessageType();
                RpcMessage rpcMessage = new RpcMessage();
                rpcMessage.setCodec(SerializationEnum.getCode(RpcConfig.getRpcConfig().getSerialization()));
                rpcMessage.setCompress(CompressEnum.getCode(RpcConfig.getRpcConfig().getCompress()));
                // 心跳检测
                if (messageType == RpcMessageTypeEnum.HEARTBEAT_REQUEST_TYPE.getCode()) {
                    rpcMessage.setMessageType(RpcMessageTypeEnum.HEARTBEAT_RESPONSE_TYPE.getCode());
                    rpcMessage.setData(RpcConstants.PONG);
                } else {
                    RpcRequest rpcRequest = (RpcRequest) ((RpcMessage) msg).getData();
                    // 执行目标方法
                    Object result = rpcRequestHandler.handle(rpcRequest);
                    log.info(String.format("result: %s", result.toString()));
                    rpcMessage.setMessageType(RpcMessageTypeEnum.RESPONSE_TYPE.getCode());
                    if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                        RpcResponse<Object> rpcResponse = RpcResponse.success(result, rpcRequest.getRequestId());
                        rpcMessage.setData(rpcResponse);
                    } else {
                        RpcResponse<Object> rpcResponse = RpcResponse.fail(RpcResponseCodeEnum.FAIL);
                        rpcMessage.setData(rpcResponse);
                        log.error("not writable now, message dropped");
                    }
                }
                ctx.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } finally {
            // 确保ByteBuf已释放，否则可能存在内存泄漏
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
            if (state == IdleState.READER_IDLE) {
                log.info("heartbeat detection timeout, closing channel");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("server catch exception");
        cause.printStackTrace();
        ctx.close();
    }
}
