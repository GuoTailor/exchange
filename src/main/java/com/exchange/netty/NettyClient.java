package com.exchange.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

/**
 * create by GYH on 2022/10/11
 */
@Slf4j
@Component
public class NettyClient {
    private final NettyServerInitializer initializer = new NettyServerInitializer();
    private final Bootstrap bootstrap = new Bootstrap();
    private Channel channel;
    private String markets;

    public NettyClient() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .channel(NioSocketChannel.class)
                .handler(initializer);
    }

    /**
     * 连接服务器, 如果已经连接就覆盖，断开以前连接
     *
     * @return {@link NettyServerHandler}
     */
    public Channel connect(MessageListener listener) throws InterruptedException, URISyntaxException {
        URI websocketURI = new URI("ws://8.212.21.207:8804/connect/json/BA5D28968C967A8A444BE72DF4FA6688");
        HttpHeaders httpHeaders = new DefaultHttpHeaders();
        //进行握手
        WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(websocketURI, WebSocketVersion.V13, null, true, httpHeaders);
        NettyServerHandler serverHandler = initializer.getServerHandler();
        serverHandler.addListener(listener);
        serverHandler.setHandshaker(handshaker);
        serverHandler.setOnClose(() -> {
            try {
                log.info("断线重联");
                connect0(websocketURI, serverHandler);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        return connect0(websocketURI, serverHandler);
    }

    private Channel connect0(URI websocketURI, NettyServerHandler serverHandler) throws InterruptedException {
        channel = bootstrap.connect(websocketURI.getHost(), websocketURI.getPort())
                .sync()
                .channel();
        serverHandler.getHandshaker().handshake(channel);
        serverHandler.handshakeFuture().sync();
        channel.eventLoop().scheduleAtFixedRate(this::heartbeat, 15, 15, TimeUnit.SECONDS);

        subscribe();
        return channel;
    }

    private void heartbeat() {
        TextWebSocketFrame frame = new TextWebSocketFrame("/heartbeat/ok");
        channel.writeAndFlush(frame);
    }

    public void setMarkets(String markets) {
        this.markets = markets;
    }

    public void getKline(String symbol, String period, Integer num) {
        TextWebSocketFrame frame = new TextWebSocketFrame("/reqk/" + symbol + "," + period + "," + num);
        log.info("获取k线 {}", frame.text());
        channel.writeAndFlush(frame);
    }

    public void subscribe() {
        TextWebSocketFrame frame = new TextWebSocketFrame("/sub/" + markets);
        log.info("订阅 {}", frame.text());
        channel.writeAndFlush(frame);
    }

    public void subscribe(String markets) {
        TextWebSocketFrame unsub = new TextWebSocketFrame("/unsub/" + this.markets);
        log.info("取消订阅 {}", unsub.text());
        channel.writeAndFlush(unsub);

        this.markets = markets;
        TextWebSocketFrame frame = new TextWebSocketFrame("/sub/" + markets);
        log.info("订阅 {}", frame.text());
        channel.writeAndFlush(frame);
    }
}
