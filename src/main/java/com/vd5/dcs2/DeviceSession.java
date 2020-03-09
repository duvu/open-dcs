package com.vd5.dcs2;

import java.util.TimeZone;

/**
 * @author beou on 10/1/18 15:37
 */
public class DeviceSession {
    private final String deviceId;

    private TimeZone timeZone;

    public DeviceSession(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }
}
