package com.vd5.dcs2.model;

import java.io.Serializable;

/**
 * @author beou on 10/2/18 21:09
 */

public class Device implements Serializable {

    private static final long serialVersionUID = -1856504047690847456L;
    private Long id;
    private String deviceId;

    private String status;


    //--

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
