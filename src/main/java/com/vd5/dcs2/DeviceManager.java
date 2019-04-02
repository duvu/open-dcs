package com.vd5.dcs2;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.vd5.dcs2.model.Device;
import com.vd5.dcs2.model.Position;
import com.vd5.dcs2.model.UnknownDevice;
import com.vd5.dcs2.model.WSMessage;
import com.vd5.feign.DeviceHub;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author beou on 10/1/18 09:12
 */
public class DeviceManager {

    private final DeviceHub deviceHub;// = DeviceHub.connect();

    private Cache<Long, Position> lastPosition = CacheBuilder.newBuilder().build();

    public DeviceManager() {
        deviceHub = DeviceHub.connect();
    }

    public Device findByUniqueId(String uniqueId) {
        return deviceHub.deviceByUniqueId(uniqueId);
    }


    public long addUnknownDevice(String uniqueId, String ipAddress, int port) {
        WSMessage msg = new WSMessage("UNKNOWNDEVICE");
        msg.setData(new UnknownDevice(uniqueId, ipAddress, port));
        ApplicationContext.getWebClient().send(msg);
        return 0l;
    }
    
    

    public void updateLastPosition(Position position) {
        lastPosition.put(position.getId(), position);
    }

    public Position getLastPosition(Long deviceId) {
        return lastPosition.getIfPresent(deviceId);
    }
}
