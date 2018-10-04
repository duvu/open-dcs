package com.vd5.dcs2.websocket;

import com.vd5.dcs2.EventLoopGroupFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author beou on 10/4/18 16:09
 */
public class WebSocketClient {
    static final String URL = "ws://127.0.0.1:8081/local";
    private final URI uri;
    private Channel channel;
    private static final EventLoopGroup group = new NioEventLoopGroup();

    public WebSocketClient() {
        this.uri = URI.create(URL);
    }

    public void open() throws Exception {
        final WebSocketClientHandler handler = new WebSocketClientHandler(
                WebSocketClientHandshakerFactory.newHandshaker(uri,WebSocketVersion.V13,null,true,
                new DefaultHttpHeaders()));

            Bootstrap bootstrap = new Bootstrap();;
            bootstrap.group(EventLoopGroupFactory.getWebsocketGroup())
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(
                                    new HttpClientCodec(),
                                    new HttpObjectAggregator(8192),
                                    WebSocketClientCompressionHandler.INSTANCE,
                                    handler
                            );
                        }
                    });
            channel = bootstrap.connect(uri.getHost(), getPort(uri)).sync().channel();
            handler.handshakeFuture().sync();

    }

    public void close() throws InterruptedException {
        channel.writeAndFlush(new CloseWebSocketFrame());
        channel.closeFuture().sync();
    }

    public void send(final String data) {
        channel.writeAndFlush(new TextWebSocketFrame(data));
    }

    //------------------------------------------------------------------------------------------------------------------
    private int getPort(URI uri) {
        int port;
        String scheme = uri.getScheme() == null? "ws" : uri.getScheme();
        if (uri.getPort() == -1) {
            if ("ws".equalsIgnoreCase(scheme)) {
                port = 80;
            } else if ("wss".equalsIgnoreCase(scheme)) {
                port = 443;
            } else {
                port = -1;
            }
        } else {
            port = uri.getPort();
        }
        return port;
    }
}
