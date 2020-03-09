package com.vd5.dcs2;

import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;

/**
 * @author beou on 9/30/18 06:39
 */
public abstract class TrackerServer {
    private final boolean duplex;
    private final int port;
    private final String host;
    private final AbstractBootstrap bootstrap;
    private final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public TrackerServer(boolean isDuplex, String protocolName) {
        duplex = isDuplex; //ApplicationContext.isDuplex(protocolName);
        host = ApplicationContext.getHost(protocolName);
        port = ApplicationContext.getPort(protocolName);

        PipelineFactory pipelineFactory = new PipelineFactory(this, protocolName) {

            @Override
            protected void addProtocolHandler(PipelineBuilder pipeline) {
                TrackerServer.this.addProtocolHandlers(pipeline);
            }
        };

        if (duplex) {
            bootstrap = new ServerBootstrap()
                    .group(EventLoopGroupFactory.getBossGroup(), EventLoopGroupFactory.getWorkerGroup())
                    .channel(NioServerSocketChannel.class)
                    .childHandler(pipelineFactory);
        } else {
            this.bootstrap = new Bootstrap()
                    .group(EventLoopGroupFactory.getWorkerGroup())
                    .channel(NioDatagramChannel.class)
                    .handler(pipelineFactory);
        }
    }

    protected abstract void addProtocolHandlers(PipelineBuilder pipelineBuilder);

    public void start() throws InterruptedException {
        Log.info("Starting *{}", port);
        InetSocketAddress endpoint;
        if (StringUtils.isNotEmpty(host)) {
            endpoint = new InetSocketAddress(host, port);
        } else {
            endpoint = new InetSocketAddress(port);
        }

        Channel channel = bootstrap.bind(endpoint).sync().channel();
        if (channel != null) {
            channelGroup.add(channel);
        }
    }

    public void stop() {
        channelGroup.close().awaitUninterruptibly();
    }

    public boolean isDuplex() {
        return duplex;
    }
    public int getPort() {
        return port;
    }
    public String getHost() {
        return host;
    }

    public AbstractBootstrap getBootstrap() {
        return bootstrap;
    }

    public ChannelGroup getChannelGroup() {
        return channelGroup;
    }
}
