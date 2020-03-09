package com.vd5.dcs2;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.vd5.dcs2.model.Device;
import com.vd5.dcs2.model.Position;
import com.vd5.dcs2.model.UnknownDevice;
import com.vd5.dcs2.model.WSMessage;
import com.vd5.feign.DeviceHub;

/**
 * @author beou on 10/1/18 09:12
 */
public class DeviceManager {

    private final DeviceHub deviceHub;// = DeviceHub.connect();

    private Cache<String, Position> lastPosition = CacheBuilder.newBuilder().build();

    public DeviceManager() {
        deviceHub = DeviceHub.connect();
    }

    public Device findByUniqueId(String uniqueId) {
        return deviceHub.deviceByUniqueId(uniqueId);
    }


    public long addUnknownDevice(String uniqueId, String ipAddress, int port) {
        WSMessage msg = new WSMessage(WSMessage.UNKNOWN_DEVICE);
        msg.setData(new UnknownDevice(uniqueId, ipAddress, port));
        ApplicationContext.getWebClient().send(msg);
        return 0l;
    }
    
    

    public void updateLastPosition(Position position) {
        lastPosition.put(position.getDeviceId(), position);
    }

    public Position getLastPosition(String deviceId) {
        return lastPosition.getIfPresent(deviceId);
    }

    public void remove(String deviceId) {

    }
}
