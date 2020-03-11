package com.vd5.dcs2;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.vd5.config.Config;
import com.vd5.dcs.geocoder.*;
import com.vd5.dcs2.model.Position;
import com.vd5.dcs2.utils.Circular;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author beou on 10/14/18 03:48
 */
public class GeocoderManager {
    private Circular<Geocoder> circular;
    private Config config;

    private Cache<String, String> geocodeCache = CacheBuilder.newBuilder().maximumSize(1000).build();

    public GeocoderManager() {
        this.config = ApplicationContext.getConfig();
        initGeocoderCircular();

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
            if (StringUtils.startsWith(x, "google")) {
                return new GoogleGeocoder(key, "en", addressFormat);
            }
            if (StringUtils.startsWith(x, "mapquest")) {
                return new MapQuestGeocoder(url, key, addressFormat);
            }
            if (StringUtils.startsWith(x, "bing")) {
                return new BingMapsGeocoder(url, key, addressFormat);
            }

            if (StringUtils.startsWith(x, "here")) {
                return new HereGeocoder(url, key, addressFormat);
            }


            // default
            return new NominatimGeocoder(url, key, "en", addressFormat);
        }).collect(Collectors.toList());

        circular = new Circular<>(geocoderList);
    }

    public void get(double latitude, double longitude, Geocoder.ReverseGeocoderCallback callback) {
        String llkey = String.format("%.6f/%.6f", latitude, longitude);
        String address = geocodeCache.getIfPresent(llkey);
        if (address != null && address.length() > 0) {
            Log.info("Geocoder: {}", "Cached!");
            callback.onSuccess(address);
        } else {
            Geocoder geocoder = circular.getOne();
            Log.info("Geocoder: {}", geocoder.getName());
            geocoder.getAddress(latitude, longitude, new Geocoder.ReverseGeocoderCallback() {
                @Override
                public void onSuccess(String address) {
                    geocodeCache.put(llkey, address);
                    callback.onSuccess(address);
                }

                @Override
                public void onFailure(Throwable e) {
                    callback.onFailure(e);
                }
            });
        }
    }
}
