package com.vd5.feign;

/**
 * @author beou on 10/8/18 20:34
 */
public class DeviceHubClientException extends RuntimeException {
    private String message; // parsed from json

    @Override
    public String getMessage() {
        return message;
    }
}
