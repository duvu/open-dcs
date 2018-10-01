package com.vd5.dcs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.asynchttpclient.AsyncHttpClient;
import static org.asynchttpclient.Dsl.*;

/**
 * @author beou on 1/11/18 15:10
 */
public class UtilsContext {
    private static AsyncHttpClient asyncHttpClient;
    private static ObjectMapper objectMapper;

    public static AsyncHttpClient getAsyncHttpClient() {
        if (asyncHttpClient == null) {
            asyncHttpClient = asyncHttpClient();
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
