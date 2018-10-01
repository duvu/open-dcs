package com.vd5.dcs2.protocol;

import io.netty.channel.Channel;

import java.net.SocketAddress;

/**
 * @author beou on 9/30/18 00:23
 */
public class ActiveDevice {
    private final long deviceId;
    private final Protocol protocol;
    private final Channel channel;
    private final SocketAddress remoteAddress;

    public ActiveDevice(long deviceId, Protocol protocol, Channel channel, SocketAddress remoteAddress) {
        this.deviceId = deviceId;
        this.protocol = protocol;
        this.channel = channel;
        this.remoteAddress = remoteAddress;
    }

    public Channel getChannel() {
        return channel;
    }

    public long getDeviceId() {
        return deviceId;
    }

    public void sendCommand() {

    }
}
