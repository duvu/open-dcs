package com.vd5.dcs2;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.SettableFuture;
import com.vd5.dcs.geocoder.*;
import com.vd5.dcs.utils.Circular;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author beou on 10/14/18 03:48
 */
public class GeocoderManager {
    private final LoadingCache<String, Optional<String>> geocoderCache;
    private Circular<Geocoder> circular;
    private Config config;

    public GeocoderManager(Config config) {
        this.config = config;
        initGeocoderCircular();

        geocoderCache = CacheBuilder.newBuilder()
                        .maximumSize(10000)
                        .expireAfterAccess(8, TimeUnit.HOURS)
                        .build(new CacheLoader<String, Optional<String>>() {

                            /**
                             * Computes or retrieves the value corresponding to {@code key}.
                             *
                             * @param s the non-null key whose value should be loaded
                             * @return the value associated with {@code key}; <b>must not be null</b>
                             * @throws Exception            if unable to load the result
                             * @throws InterruptedException if this method is interrupted. {@code InterruptedException} is
                             *                              treated like any other {@code Exception} in all respects except that, when it is caught,
                             *                              the thread's interrupt status is set
                             */
                            @Override
                            public Optional<String> load(String s) throws Exception {
                                double latitude = Double.parseDouble(s.split(",")[0]);
                                double longitude = Double.parseDouble(s.split(",")[1]);
                                SettableFuture<String> rtn = SettableFuture.create();
                                circular.getOne().getAddress(latitude, longitude, new Geocoder.ReverseGeocoderCallback() {
                                    @Override
                                    public void onSuccess(String address) {
                                        rtn.set(address);
                                    }

                                    @Override
                                    public void onFailure(Throwable e) {
                                        circular.getOne().getAddress(latitude, longitude, new Geocoder.ReverseGeocoderCallback() {

                                            @Override
                                            public void onSuccess(String address) {
                                                rtn.set(address);
                                            }

                                            @Override
                                            public void onFailure(Throwable e) {
                                                rtn.setException(e);
                                            }
                                        });
                                    }
                                });
                                return Optional.of(rtn.get());
                            }
                        });
            }

    private String[] getGeocoderList() {
        String geocoderList = StringUtils.deleteWhitespace(config.getString("geocoder.circular"));
        return StringUtils.split(geocoderList, ",");
    }
    private String getGeocoderKey(String geocoderName) {
        return config.getString("geocoder." + geocoderName + ".key");
    }

    private String getGeocoderURL(String geocoderName) {
        return config.getString("geocoder." + geocoderName + ".url");
    }

    private String getGeocoderFormat(String geocoderName) {
        return config.getString("geocoder." + geocoderName + ".format");
    }

    public Geocoder getGeocoder() {
        return circular.getOne();
    }

    private void initGeocoderCircular() {
        List<Geocoder> geocoderList = Arrays.stream(getGeocoderList()).map(x -> {
            String key = getGeocoderKey(x);
            String url = getGeocoderURL(x);
            String format = getGeocoderFormat(x);

            AddressFormat addressFormat;
            if (StringUtils.isNotEmpty(format)) {
                addressFormat = new AddressFormat(format);
            } else {
                addressFormat = new AddressFormat();
            }
            switch (x) {
                case "google":
                    return new GoogleGeocoder(key, "en", addressFormat);
                case "mapquest":
                    return new MapQuestGeocoder(url, key, addressFormat);
                case "bing":
                    return new BingMapsGeocoder(url, key, addressFormat);
                case "here":
                    return new HereGeocoder(url, key, addressFormat);
                default:
                    return new NominatimGeocoder(url, key, "en", addressFormat);
            }
        }).collect(Collectors.toList());

        circular = new Circular<>(geocoderList);
    }

    public String get(String key) {
        try {
            return geocoderCache.get(key).orElse("");
        } catch (ExecutionException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String get(double lat, double lng) {
        if (lat != 0 && lng != 0) {
            try {
                return geocoderCache.get(String.valueOf(lat) + "," + String.valueOf(lng)).orElse("");
            } catch (ExecutionException e) {
                e.printStackTrace();
                return "";
            }
        } else {
            return "";
        }
    }
}
