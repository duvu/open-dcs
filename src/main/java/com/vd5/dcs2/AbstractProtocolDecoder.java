package com.vd5.dcs2;

import com.vd5.dcs.helper.UnitsConverter;
import com.vd5.dcs2.model.Device;
import com.vd5.dcs2.model.NetworkMessage;
import com.vd5.dcs2.model.Position;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.DatagramChannel;
import io.netty.util.ReferenceCountUtil;

import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author beou on 10/1/18 09:15
 */
public abstract class AbstractProtocolDecoder extends ChannelInboundHandlerAdapter {
    private final Protocol protocol;

    private DeviceSession channelDeviceSession; // connection-based protocols
    private Map<SocketAddress, DeviceSession> addressDeviceSessions = new HashMap<>(); // connectionless protocols

    public AbstractProtocolDecoder(Protocol protocol) {
        this.protocol = protocol;
    }

    public String getProtocolName() {
        return protocol.getName();
    }

    protected DeviceSession getDeviceSession(Channel channel, SocketAddress remoteAddress, String uniqueId) {
        if (ApplicationContext.getDeviceManager().checkNotExisted(uniqueId)) {
            return null;
        }

        if (channel != null && channel.pipeline().get("httpDecoder") != null || ApplicationContext.getConfig().getBoolean("decoder.ignoreSessionCache")) {
            if (ApplicationContext.getConnectionManager() != null) {
                ApplicationContext.getConnectionManager().addActiveDevice(uniqueId, protocol, channel, remoteAddress);
            }
            return new DeviceSession(uniqueId);
        }
        if (channel instanceof DatagramChannel) {
            DeviceSession deviceSession = addressDeviceSessions.get(remoteAddress);
            if (deviceSession != null && (deviceSession.getDeviceId().equalsIgnoreCase(uniqueId))) {
                return deviceSession;
            } else {
                deviceSession = new DeviceSession(uniqueId);
                addressDeviceSessions.put(remoteAddress, deviceSession);
                if (ApplicationContext.getConnectionManager() != null) {
                    ApplicationContext.getConnectionManager().addActiveDevice(uniqueId, protocol, channel, remoteAddress);
                }
                return deviceSession;
            }
        } else {
            if (channelDeviceSession == null) {
                channelDeviceSession = new DeviceSession(uniqueId);
                if (ApplicationContext.getConnectionManager() != null) {
                    ApplicationContext.getConnectionManager().addActiveDevice(uniqueId, protocol, channel, remoteAddress);
                }
            }
            return channelDeviceSession;
        }
    }

    public void getLastLocation(Position position, Date deviceTime) {
        if (position.getDeviceId() != null) {
            position.setOutdated(true);

            Position last = ApplicationContext.getDeviceManager().getLastPosition(position.getDeviceId());
            if (last != null) {
                position.setFixTime(last.getFixTime());
                position.setValid(last.getValid());
                position.setLatitude(last.getLatitude());
                position.setLongitude(last.getLongitude());
                position.setAltitude(last.getAltitude());
                position.setSpeed(last.getSpeed());
                position.setCourse(last.getCourse());
                position.setAccuracy(last.getAccuracy());
            } else {
                position.setFixTime(new Date(0));
            }

            if (deviceTime != null) {
                position.setDeviceTime(deviceTime);
            } else {
                position.setDeviceTime(new Date());
            }
        }
    }

    private long findDeviceId(SocketAddress remoteAddress, String... uniqueIds) {
        if (uniqueIds.length > 0) {
            long deviceId = 0;
            Device device = null;
            try {
                for (String uniqueId : uniqueIds) {
                    if (uniqueId != null) {
                        device = ApplicationContext.getDeviceManager().findByUniqueId(uniqueId);
                        if (device != null) {
                            deviceId = device.getId();
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                Log.warning(e);
            }

            if (deviceId == 0) {
                InetSocketAddress inet = (InetSocketAddress) remoteAddress;
                String host = inet.getAddress().getHostAddress();
                int port = ApplicationContext.getPort(protocol.getName());
                //return ApplicationContext.getDeviceManager().addUnknownDevice(uniqueIds[0], host, port);
                return 0l;
            }
            if (device != null && !"Disabled".equalsIgnoreCase(device.getStatus())) {
                return deviceId;
            }
            StringBuilder message = new StringBuilder();
            if (deviceId == 0) {
                message.append("Unknown device -");
            } else {
                message.append("Disabled device -");
            }
            for (String uniqueId : uniqueIds) {
                message.append(" ").append(uniqueId);
            }
            if (remoteAddress != null) {
                message.append(" (").append(((InetSocketAddress) remoteAddress).getHostString()).append(")");
            }
            Log.warning(message.toString());
        }
        return 0;
    }

    protected double convertSpeed(double value, String defaultUnits) {
        switch (ApplicationContext.getConfig().getString(getProtocolName() + ".speed", defaultUnits)) {
            case "kmh":
                return UnitsConverter.knotsFromKph(value);
            case "mps":
                return UnitsConverter.knotsFromMps(value);
            case "mph":
                return UnitsConverter.knotsFromMph(value);
            case "kn":
            default:
                return value;
        }
    }

    protected double round(double value, int n) {
        BigDecimal bigDecimal = new BigDecimal(value);
        return bigDecimal.setScale(n, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    //--
    private void saveOriginal(Object decodedMessage, Object originalMessage) {
        if (decodedMessage instanceof Position) {
            Position position = (Position) decodedMessage;
            if (originalMessage instanceof ByteBuf) {
                ByteBuf buf = (ByteBuf) originalMessage;
                position.set(Position.KEY_ORIGINAL, ByteBufUtil.hexDump(buf));
            } else if (originalMessage instanceof String) {
                position.set(Position.KEY_ORIGINAL, ((String) originalMessage));
            }
        }
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
