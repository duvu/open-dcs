package com.vd5.dcs2.handler;

import com.vd5.dcs2.TrackerServer;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author beou on 10/1/18 02:52
 */
@ChannelHandler.Sharable
public final class OpenChannelHandler extends ChannelDuplexHandler {

    private final TrackerServer server;

    public OpenChannelHandler(TrackerServer server) {
        this.server = server;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        server.getChannelGroup().add(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        server.getChannelGroup().remove(ctx.channel());
    }

}
