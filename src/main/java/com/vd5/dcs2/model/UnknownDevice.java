package com.vd5.dcs2.model;

import java.io.Serializable;

/**
 * @author beou on 10/24/18 02:38
 */
public class UnknownDevice implements Serializable {
    private String uniqueId;
    private String remoteIpAddress;
    private int port;

    public UnknownDevice(String uniqueId, String remoteIpAddress, int port) {
        this.uniqueId = uniqueId;
        this.remoteIpAddress = remoteIpAddress;
        this.port = port;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getRemoteIpAddress() {
        return remoteIpAddress;
    }

    public void setRemoteIpAddress(String remoteIpAddress) {
        this.remoteIpAddress = remoteIpAddress;
    }
}
