package com.vd5.dcs2.handler;

import com.google.gson.Gson;
import com.vd5.dcs2.AbstractProtocolDecoder;
import com.vd5.dcs2.ApplicationContext;
import com.vd5.dcs2.Log;
import com.vd5.dcs2.model.Position;
import com.vd5.dcs2.model.WSMessage;
import com.vd5.dcs2.websocket.WebSocketClient;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.DatagramChannel;
import io.netty.handler.timeout.IdleStateEvent;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * @author beou on 10/1/18 04:02
 */
public class MainEventHandler extends ChannelInboundHandlerAdapter {
    private final Set<String> connectionlessProtocols = new HashSet<>();
    private final Gson gson = new Gson();
    private static String formatChannel(Channel channel) {
        return String.format("[%s]", channel.id().asShortText());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Log.info(formatChannel(ctx.channel()) + " connected");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Position) {
            Position position = (Position) msg;
            WSMessage wsMessage = new WSMessage("EVENTDATA");
            wsMessage.setData(position);
            ApplicationContext.getWebClient().send(gson.toJson(wsMessage));
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Log.info(formatChannel(ctx.channel()) + " disconnected");
        closeChannel(ctx.channel());

        AbstractProtocolDecoder protocolDecoder = (AbstractProtocolDecoder) ctx.pipeline().get("objectDecoder");
        if (ctx.pipeline().get("httpDecoder") == null
                && !connectionlessProtocols.contains(protocolDecoder.getProtocolName())) {
            ApplicationContext.getConnectionManager().removeActiveDevice(ctx.channel());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Log.error(formatChannel(ctx.channel()) + " error", cause);
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
