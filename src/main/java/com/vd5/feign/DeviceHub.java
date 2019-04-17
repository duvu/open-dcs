package com.vd5.feign;

import com.vd5.dcs2.model.Device;
import feign.Feign;
import feign.Logger;
import feign.Param;
import feign.RequestLine;
import feign.codec.Decoder;
import feign.gson.GsonDecoder;

/**
 * @author beou on 10/8/18 15:07
 */
public interface DeviceHub {
    @RequestLine("GET /internal/device/{id}")
    Device device(@Param("id") Long id);

    @RequestLine("GET /internal/device?uniqueId={uniqueId}")
    Device deviceByUniqueId(@Param("uniqueId") String uniqueId);

    static DeviceHub connect() {
        Decoder decoder = new GsonDecoder();
        return Feign.builder()
                .decoder(decoder)
                .errorDecoder(new DeviceErrorDecoder(decoder))
                .logger(new Logger.ErrorLogger())
                .logLevel(Logger.Level.BASIC)
                .target(DeviceHub.class, "http://localhost:8081");
    }

}


//
