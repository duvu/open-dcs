/*
 * Copyright 2015 - 2017 Anton Tananaev (anton@traccar.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vd5.dcs.geocoder;

import com.vd5.dcs.UtilsContext;
import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.traccar.Context;
//import org.traccar.helper.Log;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public abstract class JsonGeocoder implements Geocoder {
    private Logger LOG = LoggerFactory.getLogger(getClass());

    private final String url;
    private final AddressFormat addressFormat;
    public JsonGeocoder(String url, AddressFormat addressFormat) {
        this.url = url;
        this.addressFormat = addressFormat;
    }

    private String handleResponse(double latitude, double longitude, Response response, ReverseGeocoderCallback callback) throws IOException {
        try (JsonReader reader = Json.createReader(response.getResponseBodyAsStream())) {
            Address address = parseAddress(reader.readObject());
            if (address != null) {
                String formattedAddress = addressFormat.format(address);
                if (callback != null) {
                    callback.onSuccess(formattedAddress);
                }
                return formattedAddress;
            } else {
                if (callback != null) {
                    callback.onFailure(new GeocoderException("Empty address"));
                }
                //Log.warning("Empty address");
            }
        }
        return null;
    }

    @Override
    public String getAddress(final double latitude, final double longitude, final ReverseGeocoderCallback callback) {
        if (callback != null) {
            UtilsContext.getAsyncHttpClient().prepareGet(String.format(url, latitude, longitude))
                    .execute(new AsyncCompletionHandler() {
                @Override
                public Object onCompleted(Response response) throws Exception {
                    return handleResponse(latitude, longitude, response, callback);
                }

                @Override
                public void onThrowable(Throwable t) {
                    callback.onFailure(t);
                }
            });
        } else {
            try {
                Response response = UtilsContext.getAsyncHttpClient()
                        .prepareGet(String.format(url, latitude, longitude)).execute().get();
                return handleResponse(latitude, longitude, response, null);
            } catch (InterruptedException | ExecutionException | IOException error) {
                //Log.warning("Geocoding failed", error);
            }
        }
        return null;
    }

    public abstract Address parseAddress(JsonObject json);

}
