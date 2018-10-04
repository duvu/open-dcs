//package com.vd5.dcs2;
//
//import com.google.common.cache.Cache;
//import com.google.common.cache.CacheBuilder;
//import com.google.common.cache.CacheLoader;
//import com.google.common.cache.LoadingCache;
//import com.vd5.dcs2.ApplicationContext;
//import com.vd5.dcs2.model.Device;
//import org.asynchttpclient.AsyncCompletionHandler;
//
//import javax.annotation.Nonnull;
//import java.util.Optional;
//
///**
// * @author beou on 10/1/18 09:12
// */
//public class DeviceManager {
//
//    LoadingCache<String, Optional<Device>> deviceCache = CacheBuilder.newBuilder().build(
//            new CacheLoader<String, Optional<Device>>() {
//
//                @Override
//                public Optional<Device> load(@Nonnull String uniqueId) throws Exception {
//                    ApplicationContext.getAsyncHttpClient().prepareGet("http://localhost:8081/local/" + uniqueId +"?token=mytoKenForLoCaL")
//                            .execute(new AsyncCompletionHandler() {
//
//                            });
//                    return Optional.ofNullable(null);
//                }
//            }
//    );
//}
