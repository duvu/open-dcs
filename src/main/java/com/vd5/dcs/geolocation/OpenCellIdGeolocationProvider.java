/*
 * Copyright 2015 Anton Tananaev (anton@traccar.org)
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
package com.vd5.dcs.geolocation;

import com.vd5.dcs.UtilsContext;
import com.vd5.dcs.model.CellTower;
import com.vd5.dcs.model.Network;
import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.Response;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class OpenCellIdGeolocationProvider implements GeolocationProvider {

    private String url;

    public OpenCellIdGeolocationProvider(String key) {
        this("http://opencellid.org/cell/get", key);
    }

    public OpenCellIdGeolocationProvider(String url, String key) {
        this.url = url + "?format=json&mcc=%d&mnc=%d&lac=%d&cellid=%d&key=" + key;
    }

    @Override
    public void getLocation(Network network, final LocationProviderCallback callback) {
        if (network.getCellTowers() != null && !network.getCellTowers().isEmpty()) {

            CellTower cellTower = network.getCellTowers().iterator().next();
            String request = String.format(url, cellTower.getMcc(), cellTower.getMnc(),
                    cellTower.getLac(), cellTower.getCid());

            UtilsContext.getAsyncHttpClient().prepareGet(request).execute(new AsyncCompletionHandler() {
                @Override
                public Object onCompleted(Response response) throws Exception {
                    try (JsonReader reader = Json.createReader(response.getResponseBodyAsStream())) {
                        JsonObject json = reader.readObject();
                        if (json.containsKey("lat") && json.containsKey("lon")) {
                            callback.onSuccess(
                                    json.getJsonNumber("lat").doubleValue(),
                                    json.getJsonNumber("lon").doubleValue(), 0);
                        } else {
                            callback.onFailure(new GeolocationException("Coordinates are missing"));
                        }
                    }
                    return null;
                }

                @Override
                public void onThrowable(Throwable t) {
                    callback.onFailure(t);
                }
            });

        } else {
            callback.onFailure(new GeolocationException("No network information"));
        }
    }

}
