package com.vd5.dcs2.protocol;

import com.vd5.dcs2.ApplicationContext;
import com.vd5.dcs2.Log;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;

/**
 * @author beou on 9/30/18 06:39
 */
public abstract class TrackerServer {
    private final boolean duplex;
    private int port;
    private String host;
    private final AbstractBootstrap bootstrap;
    private final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public TrackerServer(boolean duplex, String protocol) {
        this.duplex = duplex;

        host = ApplicationContext.getHost(protocol);
        port = ApplicationContext.getPort(protocol);

        PipelineFactory pipelineFactory = new PipelineFactory(this, protocol) {

            @Override
            protected void addProtocolHandler(PipelineBuilder pipelineBuilder) {
                TrackerServer.this.addProtocolHandlers(pipelineBuilder);
            }
        };

        if (duplex) {
            bootstrap = new ServerBootstrap()
                    .group(EventLoopGroupFactory.getBossGroup(), EventLoopGroupFactory.getWorkerGroup())
                    .channel(NioServerSocketChannel.class)
                    .childHandler(pipelineFactory);
        } else {
            bootstrap = new Bootstrap();
            bootstrap.group(EventLoopGroupFactory.getEpollGroup())
                    .channel(EpollDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .option(EpollChannelOption.SO_REUSEPORT, true)
                    .handler(pipelineFactory);
        }
    }

    protected abstract void addProtocolHandlers(PipelineBuilder pipelineBuilder);

    public void start() throws InterruptedException {
        Log.info("Starting *"+port);
        InetSocketAddress enpoint;
        if (StringUtils.isNotEmpty(host)) {
            enpoint = new InetSocketAddress(host, port);
        } else {
            enpoint = new InetSocketAddress(port);
        }

        if (duplex) {
            Channel channel = bootstrap.bind(enpoint).sync().channel();
            if (channel != null) {
                channelGroup.add(channel);
            }
        } else {
            for (int i = 0; i < ApplicationContext.getWorkerNThread(); i++) {
                Channel channel= bootstrap.bind(enpoint).sync().channel();
                channelGroup.add(channel);
            }
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

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public AbstractBootstrap getBootstrap() {
        return bootstrap;
    }

    public ChannelGroup getChannelGroup() {
        return channelGroup;
    }
}
