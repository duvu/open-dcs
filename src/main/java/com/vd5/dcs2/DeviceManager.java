package com.vd5.dcs2;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.vd5.dcs2.model.Device;
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

    public DeviceManager() {
        deviceHub = DeviceHub.connect();
    }

    public Device findByUniqueId(String uniqueId) {
        try {
            return deviceCache.get(uniqueId).orElse(null);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public long addUnknownDevice(String uniqueId, String ipAddress, int port) {
        WSMessage msg = new WSMessage("UNKNOWNDEVICE");
        msg.setData(new UnknownDevice(uniqueId, ipAddress, port));
        ApplicationContext.getWebClient().send(msg);
        return 0l;
    }
}
