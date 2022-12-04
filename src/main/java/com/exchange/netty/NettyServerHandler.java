package com.exchange.netty;

import com.exchange.netty.dto.GeneralMarket;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * create by GYH on 2022/10/11
 */
@ChannelHandler.Sharable
public class NettyServerHandler extends SimpleChannelInboundHandler<Object> {
    private final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class.getSimpleName());
    private final ObjectMapper json = new ObjectMapper();
    private MessageListener onMessage;
    private CloseListener onClose;
    private WebSocketClientHandshaker handshaker;

    private ChannelPromise handshakeFuture;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        handshakeFuture = ctx.newPromise();
    }

    public ChannelFuture handshakeFuture() {
        return handshakeFuture;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        logger.info("通道打开");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        if (onClose != null) {
            onClose.onClose();
        }
        logger.info("连接关闭");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        logger.info("通道异常{}", cause.getMessage());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if (!handshaker.isHandshakeComplete()) {
            try {
                handshaker.finishHandshake(ctx.channel(), (FullHttpResponse) msg);
                logger.info("websocket client 连接成功");
                handshakeFuture.setSuccess();
            } catch (WebSocketHandshakeException e) {
                logger.info("websocket client 连接失败");
                handshakeFuture.setFailure(e);
            }
            return;
        }
        if (msg instanceof FullHttpResponse response) {
            throw new IllegalStateException(
                    "Unexpected FullHttpResponse (getStatus=" + response.status() +
                            ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')');
        }
        if (msg instanceof TextWebSocketFrame textFrame) {
//            logger.info("websocket client 接收到的消息：{}", textFrame.text());
            try {
                GeneralMarket generalMarket = json.readValue(textFrame.text(), GeneralMarket.class);
                if (onMessage != null) {
                    onMessage.onMessage(generalMarket);
                }
            } catch (JsonProcessingException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else if (msg instanceof PongWebSocketFrame) {
            logger.info("WebSocket Client received pong");
        } else if (msg instanceof CloseWebSocketFrame) {
            logger.info("websocket client关闭");
        }
    }

    public void addListener(MessageListener listener) {
        onMessage = listener;
    }

    public void setOnClose(CloseListener listener) {
        onClose = listener;
    }

    public void setHandshaker(WebSocketClientHandshaker handshaker) {
        this.handshaker = handshaker;
    }

    public WebSocketClientHandshaker getHandshaker() {
        return handshaker;
    }
}
