package com.vd5.dcs2.protocol;

import com.vd5.dcs2.ApplicationContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

/**
 * @author beou on 10/1/18 00:32
 */
public final class EventLoopGroupFactory {
    private static EventLoopGroup bossGroup = new NioEventLoopGroup(ApplicationContext.getBossNThread());
    private static EventLoopGroup workerGroup = new NioEventLoopGroup(ApplicationContext.getWorkerNThread());
    private static EpollEventLoopGroup epollGroup = new EpollEventLoopGroup(ApplicationContext.getWorkerNThread());

    public static EventLoopGroup getBossGroup() {
        return bossGroup;
    }

    public static EventLoopGroup getWorkerGroup() {
        return workerGroup;
    }

    public static EpollEventLoopGroup getEpollGroup() {
        return epollGroup;
    }
}
