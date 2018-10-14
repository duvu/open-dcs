package com.vd5.dcs2.protocol.wlink;

import com.vd5.dcs2.AbstractProtocolDecoder;
import com.vd5.dcs2.Protocol;
import com.vd5.dcs2.model.Position;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;

/**
 * @author beou on 10/14/18 23:31
 */
public class WlinkProtocolDecoder extends AbstractProtocolDecoder {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    public WlinkProtocolDecoder(Protocol protocol) {
        super(protocol);
    }

    @Override
    protected Object decode(Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {
        if (msg instanceof String) {
            log.info("Got data: " + msg);
        }

        return new Position();
    }
}
