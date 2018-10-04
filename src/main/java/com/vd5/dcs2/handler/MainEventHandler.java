package com.vd5.dcs2.handler;

import com.vd5.dcs2.Log;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.DatagramChannel;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @author beou on 10/1/18 04:02
 */
public class MainEventHandler extends ChannelInboundHandlerAdapter {

    private static String formatChannel(Channel channel) {
        return String.format("[%s]", channel.id().asShortText());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Log.info(formatChannel(ctx.channel()) + " connected");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Log.info(formatChannel(ctx.channel()) + " disconnected");
        closeChannel(ctx.channel());

//        BaseProtocolDecoder protocolDecoder = (BaseProtocolDecoder) ctx.pipeline().get("objectDecoder");
//        if (ctx.pipeline().get("httpDecoder") == null
//                && !connectionlessProtocols.contains(protocolDecoder.getProtocolName())) {
//            Context.getConnectionManager().removeActiveDevice(ctx.channel());
//        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Log.warning(formatChannel(ctx.channel()) + " error", cause);
        closeChannel(ctx.channel());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            Log.info(formatChannel(ctx.channel()) + " timed out");
            closeChannel(ctx.channel());
        }
    }
    private void closeChannel(Channel channel) {
        if (!(channel instanceof DatagramChannel)) {
            channel.close();
        }
    }
}
