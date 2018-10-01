package com.vd5.dcs2.model;

import java.net.SocketAddress;

/**
 * @author beou on 10/1/18 02:41
 */
public class NetworkMessage {
    private final SocketAddress remoteAddress;
    private final Object message;

    public NetworkMessage(Object message, SocketAddress remoteAddress) {
        this.message = message;
        this.remoteAddress = remoteAddress;
    }

    public SocketAddress getRemoteAddress() {
        return remoteAddress;
    }

    public Object getMessage() {
        return message;
    }
}
