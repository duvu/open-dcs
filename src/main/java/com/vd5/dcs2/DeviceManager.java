package com.vd5.dcs2;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.vd5.dcs2.model.Device;
import com.vd5.dcs2.model.Position;
import com.vd5.dcs2.model.UnknownDevice;
import com.vd5.dcs2.websocket.WSMessage;
import com.vd5.feign.DeviceHub;

import java.util.concurrent.TimeUnit;

/**
 * @author beou on 10/1/18 09:12
 */
public class DeviceManager {

    private final DeviceHub deviceHub;// = DeviceHub.connect();

    private Cache<String, Position> lastPosition = CacheBuilder.newBuilder().build();
    private final Cache<String, String> notExistedRegistry = CacheBuilder
            .newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    public DeviceManager() {
        deviceHub = DeviceHub.connect();
    }

    public Device findByUniqueId(String uniqueId) {
        return deviceHub.deviceByUniqueId(uniqueId);
    }

    public Position getLastPosition(String deviceId) {
        return lastPosition.getIfPresent(deviceId);
    }

    public void remove(String deviceId) {

    }

    public void notExisted(String deviceId) {
        notExistedRegistry.put(deviceId, deviceId);
    }

    public boolean checkNotExisted(String deviceId) {
        String deviceIdd = notExistedRegistry.getIfPresent(deviceId);
        return deviceIdd != null && deviceIdd.length() > 0;
    }
}
