package com.vd5.dcs2.model;

/**
 * @author beou on 10/1/18 15:53
 */
public class ProtocolObject {
    private String clazz;
    private String host;
    private int port;
    private boolean enabled;
    private boolean duplex;
    private int timeout;
    private double minSpeedKph;

    private boolean decodeLow;

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isDuplex() {
        return duplex;
    }

    public void setDuplex(boolean duplex) {
        this.duplex = duplex;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public double getMinSpeedKph() {
        return minSpeedKph;
    }

    public void setMinSpeedKph(double minSpeedKph) {
        this.minSpeedKph = minSpeedKph;
    }

    public boolean isDecodeLow() {
        return decodeLow;
    }

    public void setDecodeLow(boolean decodeLow) {
        this.decodeLow = decodeLow;
    }
}
