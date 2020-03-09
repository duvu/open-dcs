package com.vd5.dcs2;

import com.vd5.config.Config;
import com.vd5.dcs.geocoder.*;
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

    public void get(double latitude, double longitude, Geocoder.ReverseGeocoderCallback callback) {
        circular.getOne().getAddress(latitude, longitude, callback);
    }
}
