package com.lzb.remoting.client.netty;


import com.lzb.enums.CompressEnum;
import com.lzb.enums.RpcMessageTypeEnum;
import com.lzb.enums.SerializationEnum;
import com.lzb.factory.SingletonFactory;
import com.lzb.registry.ServiceDiscovery;
import com.lzb.remoting.client.ChannelPool;
import com.lzb.remoting.client.RpcClient;
import com.lzb.remoting.client.UnprocessedRequests;
import com.lzb.remoting.codec.RpcMessageDecoder;
import com.lzb.remoting.codec.RpcMessageEncoder;
import com.lzb.remoting.dto.RpcMessage;
import com.lzb.remoting.dto.RpcRequest;
import com.lzb.remoting.dto.RpcResponse;
import com.lzb.serviceloader.ServiceLoader;
import com.lzb.config.RpcConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;


@Slf4j
public final class NettyRpcClient implements RpcClient {
    private final ServiceDiscovery serviceDiscovery;
    private final UnprocessedRequests unprocessedRequests;
    private final ChannelPool channelPool;
    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;

    public NettyRpcClient() {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        // 心跳检测
                        pipeline.addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
                        // 编解码器
                        pipeline.addLast(new RpcMessageEncoder());
                        pipeline.addLast(new RpcMessageDecoder());
                        pipeline.addLast(new NettyRpcClientHandler());
                    }
                });
        this.serviceDiscovery = ServiceLoader.getServiceLoader(ServiceDiscovery.class).getService(RpcConfig.getRpcConfig().getServiceDiscovery());
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
        this.channelPool = SingletonFactory.getInstance(ChannelPool.class);
    }


    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        CompletableFuture<RpcResponse<Object>> resultFuture = new CompletableFuture<>();
        // 服务发现
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest);
        // 获取 Channel
        Channel channel = getChannel(inetSocketAddress);
        if (channel.isActive()) {
            // 记录未完成任务
            unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);
            RpcMessage rpcMessage = RpcMessage.builder().data(rpcRequest)
                    // 序列化
                    .codec(SerializationEnum.HESSIAN.getCode())
                    // 压缩
                    .compress(CompressEnum.GZIP.getCode())
                    // 请求类型
                    .messageType(RpcMessageTypeEnum.REQUEST_TYPE.getCode())
                    .build();
            channel.writeAndFlush(rpcMessage).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("Client sends messages: [{}]", rpcMessage);
                } else {
                    future.channel().close();
                    resultFuture.completeExceptionally(future.cause());
                    log.error("Sending message failed: ", future.cause());
                }
            });
        } else {
            throw new IllegalStateException();
        }
        return resultFuture;
    }

    public Channel getChannel(InetSocketAddress inetSocketAddress) {
        Channel channel = channelPool.get(inetSocketAddress);
        if (channel == null) {
            channel = doConnect(inetSocketAddress);
            channelPool.set(inetSocketAddress, channel);
        }
        return channel;
    }

    /**
     * 连接服务端
     */
    @SneakyThrows
    public Channel doConnect(InetSocketAddress inetSocketAddress) {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        // 监听连接
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("Successfully connected the client to the server [{}] !", inetSocketAddress.toString());
                completableFuture.complete(future.channel());
            } else {
                throw new IllegalStateException();
            }
        });
        return completableFuture.get();
    }

    public void close() {
        eventLoopGroup.shutdownGracefully();
    }
}
