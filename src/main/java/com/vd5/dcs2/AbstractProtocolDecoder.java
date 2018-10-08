package com.vd5.dcs2;

import com.vd5.dcs2.model.NetworkMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

/**
 * @author beou on 10/1/18 09:15
 */
public abstract class AbstractProtocolDecoder extends ChannelInboundHandlerAdapter {
    private final Protocol protocol;

    public AbstractProtocolDecoder(Protocol protocol) {
        this.protocol = protocol;
    }

    public String getProtocolName() {
        return protocol.getName();
    }

    public DeviceSession getDeviceSession(Channel channel, SocketAddress remoteAddress, String... uniqueIds) {
        return null;
    }

    //--
    private void saveOriginal(Object decodedMessage, Object originalMessage) {
//        if (Context.getConfig().getBoolean("database.saveOriginal") && decodedMessage instanceof Position) {
//            Position position = (Position) decodedMessage;
//            if (originalMessage instanceof ByteBuf) {
//                ByteBuf buf = (ByteBuf) originalMessage;
//                position.set(Position.KEY_ORIGINAL, ByteBufUtil.hexDump(buf));
//            } else if (originalMessage instanceof String) {
//                position.set(Position.KEY_ORIGINAL, DataConverter.printHex(
//                        ((String) originalMessage).getBytes(StandardCharsets.US_ASCII)));
//            }
//        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NetworkMessage networkMessage = (NetworkMessage) msg;
        Object originalMessage = networkMessage.getMessage();
        try {
            Object decodedMessage = decode(ctx.channel(), networkMessage.getRemoteAddress(), originalMessage);
            onMessageEvent(ctx.channel(), networkMessage.getRemoteAddress(), originalMessage, decodedMessage);
            if (decodedMessage == null) {
                decodedMessage = handleEmptyMessage(ctx.channel(), networkMessage.getRemoteAddress(), originalMessage);
            }
            if (decodedMessage != null) {
                if (decodedMessage instanceof Collection) {
                    for (Object o : (Collection) decodedMessage) {
                        saveOriginal(o, originalMessage);
                        ctx.fireChannelRead(o);
                    }
                } else {
                    saveOriginal(decodedMessage, originalMessage);
                    ctx.fireChannelRead(decodedMessage);
                }
            }
        } finally {
            ReferenceCountUtil.release(originalMessage);
        }
    }

    protected void onMessageEvent(
            Channel channel, SocketAddress remoteAddress, Object originalMessage, Object decodedMessage) {
    }

    protected Object handleEmptyMessage(Channel channel, SocketAddress remoteAddress, Object msg) {
        return null;
    }

    protected abstract Object decode(Channel channel, SocketAddress remoteAddress, Object msg) throws Exception;

}
