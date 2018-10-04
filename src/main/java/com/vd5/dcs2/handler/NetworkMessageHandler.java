package com.vd5.dcs2.handler;

import com.vd5.dcs2.model.NetworkMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetSocketAddress;

/**
 * @author beou on 10/1/18 02:50
 */
@ChannelHandler.Sharable
public class NetworkMessageHandler  extends ChannelDuplexHandler {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (ctx.channel() instanceof DatagramChannel) {
            DatagramPacket packet = (DatagramPacket) msg;
            ctx.fireChannelRead(new NetworkMessage(packet.content(), packet.sender()));
        } else {
            ByteBuf buffer = (ByteBuf) msg;
            ctx.fireChannelRead(new NetworkMessage(buffer, ctx.channel().remoteAddress()));
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        NetworkMessage message = (NetworkMessage) msg;
        if (ctx.channel() instanceof DatagramChannel) {
            InetSocketAddress recipient = (InetSocketAddress) message.getRemoteAddress();
            InetSocketAddress sender = (InetSocketAddress) ctx.channel().localAddress();
            ctx.write(new DatagramPacket((ByteBuf) message.getMessage(), recipient, sender), promise);
        } else {
            ctx.write(message.getMessage(), promise);
        }
    }

}