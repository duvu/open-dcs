package com.vd5.dcs2;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.net.SocketAddress;

/**
 * @author beou on 10/1/18 09:15
 */
public class AbstractProtocolDecoder extends ChannelInboundHandlerAdapter {
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
}
