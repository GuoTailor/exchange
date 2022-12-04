package com.exchange.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;

/**
 * Created by GYH on 2018/12/3.
 */
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {

    private final NettyServerHandler serverHandler = new NettyServerHandler();


    /**
     * 初始化channel
     */
    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //pipeline.addLast(new IdleStateHandler(60 * 3, 0, 0));
        pipeline.addLast(new HttpClientCodec(), new HttpObjectAggregator(1024 * 1024 * 10));
        pipeline.addLast(serverHandler);
    }

    public NettyServerHandler getServerHandler() {
        return serverHandler;
    }
}
