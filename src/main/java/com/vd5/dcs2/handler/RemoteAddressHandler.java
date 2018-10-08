package com.vd5.dcs2.handler;

import com.vd5.dcs2.model.Position;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.net.InetSocketAddress;

/**
 * @author beou on 10/1/18 02:45
 */
@ChannelHandler.Sharable
public class RemoteAddressHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        InetSocketAddress remoteAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        String hostAddress = remoteAddress != null ? remoteAddress.getAddress().getHostAddress() : null;

        if (msg instanceof Position) {
            Position position = (Position) msg;
            position.set(Position.KEY_IP, hostAddress);
        }

        ctx.fireChannelRead(msg);
    }

}
