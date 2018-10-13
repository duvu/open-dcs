package com.vd5.dcs2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vd5.dcs.geocoder.*;
import com.vd5.dcs.utils.Circular;
import com.vd5.dcs2.model.ProtocolObject;
import com.vd5.dcs2.websocket.WebSocketClient;
import org.apache.commons.lang3.StringUtils;
import org.asynchttpclient.AsyncHttpClient;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.asynchttpclient.Dsl.asyncHttpClient;

/**
 * @author beou on 10/1/18 01:08
 */
public final class ApplicationContext {
    private static final String DCS_BOSS_N_THREAD = "dcs.boss-n-thread";
    private static final String DCS_WORKER_N_THREAD = "dcs.worker-n-thread";

    private static Config config;


    private static ServerManager serverManager;
    private static AsyncHttpClient asyncHttpClient;
    private static ObjectMapper objectMapper;
    private static ConnectionManager connectionManager;
    private static DeviceManager deviceManager;
    private static GeocoderManager geocoderManager;

    private static WebSocketClient webClient;

    private static final String EXTERNAL_CONFIG_FILE = "app.conf";

    private ApplicationContext() {}

    static {
        config = new Config();


        try {
            config.load(EXTERNAL_CONFIG_FILE);

            serverManager = new ServerManager();
            connectionManager = new ConnectionManager();
            deviceManager = new DeviceManager();
            geocoderManager = new GeocoderManager(config);

        } catch (IOException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }


        //--------------------------
        // Geocoder circular
        //--------------------------
        //initGeocoderCircular();
        //--------------------------//
    }

    public static Config getConfig() {
        return config;
    }

    //------
    //------
    public static int getBossNThread() {
        return config.getInteger(DCS_BOSS_N_THREAD, 2);
    }

    public static void setBossNThread(int nThread) {
        config.setString(DCS_BOSS_N_THREAD, String.valueOf(nThread));
    }

    public static int getWorkerNThread() {
        return config.getInteger(DCS_WORKER_N_THREAD, 8);
    }

    public static void setWorkerNThread(int nThread) {
        config.setString(DCS_WORKER_N_THREAD, String.valueOf(nThread));
    }
    //---
    // Geocoder
    //---
    public static int getGeocoderReuseDistance() {
        return config.getInteger("geocoder.reuseDistance", 0);
    }

    public static boolean getProcessInvalidPosition() {
        return config.getBoolean("geocoder.processInvalidPositions");
    }

    public static GeocoderManager getGeocoderManager() {
        return geocoderManager;
    }



    //--load protocol from config
    public static String[] getProtocolNameList() {
        return StringUtils.split(config.getString("protocol.available"), ", ");
    }
    public static ProtocolObject getProtocolObject(String protocolName) {
        ProtocolObject object = new ProtocolObject();
        object.setClazz(config.getString("protocol." + protocolName + ".clazz"));
        object.setDuplex(config.getBoolean("protocol." + protocolName + ".duplex"));
        object.setEnabled(config.getBoolean("protocol." + protocolName + ".enabled"));
        object.setHost(config.getString("protocol." + protocolName + ".host"));
        object.setPort(config.getInteger("protocol." + protocolName + ".port"));
        object.setTimeout(config.getInteger("protocol." + protocolName + ".timeout"));
        object.setMinSpeedKph(config.getDouble("protocol." + protocolName + ".min-speed"));
        object.setDecodeLow(config.getBoolean("protocol." + protocolName + ".decode-low"));
        return object;
    }

    public static int getTimeout(String protocol) {
        return config.getInteger(protocol + ".timeout");
    }
    public static String getHost(String protocolName) {
        return config.getString("protocol." + protocolName + ".host");
    }
    public static int getPort(String protocolName) {
        return config.getInteger("protocol." + protocolName + ".port");
    }

    //------------------------------------------------------------------------------------------------------------------
    public static ServerManager getServerManager() {
        return serverManager;
    }

    public static ConnectionManager getConnectionManager() {
        return connectionManager;
    }
    public static DeviceManager getDeviceManager() {
        return deviceManager;
    }
    //--
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

    public static WebSocketClient getWebClient() {
        if (webClient == null) {
            webClient = new WebSocketClient();
        } else if (webClient.isClose()) {
            try {
                webClient.open();
            } catch (Exception e) {
                webClient.close();
                Log.error("Not able to open websocket-secsion", e);
            }
        }
        return webClient;
    }
}
