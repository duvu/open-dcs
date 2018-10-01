package com.vd5.dcs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ning.http.client.AsyncHttpClient;

/**
 * @author beou on 1/11/18 15:10
 */
public class UtilsContext {
    private static AsyncHttpClient asyncHttpClient;
    private static ObjectMapper objectMapper;

    public static AsyncHttpClient getAsyncHttpClient() {
        if (asyncHttpClient == null) {
            asyncHttpClient = new AsyncHttpClient();
        }
        return asyncHttpClient;
    }

    public static ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
        return objectMapper;
    }


}
