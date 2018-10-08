package com.vd5.dcs2;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.vd5.dcs2.model.Device;
import com.vd5.feign.DeviceHub;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * @author beou on 10/1/18 09:12
 */
public class DeviceManager {

    private final DeviceHub deviceHub = DeviceHub.connect();
    LoadingCache<String, Optional<Device>> deviceCache = CacheBuilder.newBuilder().build(
            new CacheLoader<String, Optional<Device>>() {

                @Override
                public Optional<Device> load(@Nonnull String uniqueId) throws Exception {
                    Device device = deviceHub.deviceByUniqueId(uniqueId);
                    return Optional.ofNullable(device);
                }
            }
    );

    public Device findByUniqueId(String uniqueId) {
        return deviceHub.deviceByUniqueId(uniqueId);
    }

    public long addUnknownDevice(String uniqueId) {
        return 0l;
    }
}
