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
    private LoadingCache<String, Optional<Device>> deviceCache = CacheBuilder.newBuilder()
            .expireAfterAccess(8, TimeUnit.HOURS)
            .maximumSize(1000)
            .build(
            new CacheLoader<String, Optional<Device>>() {

                @Override
                public Optional<Device> load(@Nonnull String uniqueId) throws Exception {

                    try {
                        Device device = deviceHub.deviceByUniqueId(uniqueId);
                        return Optional.ofNullable(device);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        return Optional.empty();
                    }
                }
            }
    );

    private Cache<Long, Position> lastPosition = CacheBuilder.newBuilder().build();

    public DeviceManager() {
        deviceHub = DeviceHub.connect();
    }

    public Device findByUniqueId(String uniqueId) {
        try {
            Optional<Device> opt = deviceCache.get(uniqueId);
            if (opt.isPresent()) {
                return opt.get();
            } else {
                deviceCache.invalidate(uniqueId);
                return null;
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void remove(String uniqueId) {
        deviceCache.invalidate(uniqueId);
    }

    public void remove(Long id) {
        deviceCache.asMap().values().forEach(x -> {
            if (x.isPresent() && x.get().getId().equals(id)) {
                deviceCache.invalidate(x.get().getDeviceId());
            }
        });
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
