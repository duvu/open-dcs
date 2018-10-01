package com.vd5.dcs2.protocol;

import java.util.TimeZone;

/**
 * @author beou on 10/1/18 15:37
 */
public class DeviceSession {
    private final Long deviceId;
    private TimeZone timeZone;

    public DeviceSession(Long deviceId) {
        this.deviceId = deviceId;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }
}
