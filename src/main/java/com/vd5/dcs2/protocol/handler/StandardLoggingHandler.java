package com.vd5.dcs2.protocol.handler;

import com.vd5.dcs2.Log;
import com.vd5.dcs2.model.NetworkMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import java.net.InetSocketAddress;

/**
 * @author beou on 10/1/18 02:44
 */
public class StandardLoggingHandler extends ChannelDuplexHandler {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log(ctx, false, msg);
        super.channelRead(ctx, msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        log(ctx, true, msg);
        super.write(ctx, msg, promise);
    }

    public void log(ChannelHandlerContext ctx, boolean downstream, Object o) {
        NetworkMessage networkMessage = (NetworkMessage) o;
        StringBuilder message = new StringBuilder();

        message.append("[").append(ctx.channel().id().asShortText()).append(": ");
        message.append(((InetSocketAddress) ctx.channel().localAddress()).getPort());
        if (downstream) {
            message.append(" > ");
        } else {
            message.append(" < ");
        }

        if (networkMessage.getRemoteAddress() != null) {
            message.append(((InetSocketAddress) networkMessage.getRemoteAddress()).getHostString());
        } else {
            message.append("null");
        }
        message.append("]");

        message.append(" HEX: ");
        message.append(ByteBufUtil.hexDump((ByteBuf) networkMessage.getMessage()));

        Log.debug(message.toString());
    }

}
