package com.vd5.dcs.geocoder;

import javax.json.JsonObject;

/**
 * @author beou on 3/23/18 16:21
 */
public class PhotonGeocoder extends JsonGeocoder {

    public PhotonGeocoder(String url, int cacheSize, AddressFormat addressFormat) {
        super(url, addressFormat);
    }

    @Override
    public Address parseAddress(JsonObject json) {
        return null;
    }
}
