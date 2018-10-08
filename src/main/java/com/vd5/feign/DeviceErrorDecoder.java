package com.vd5.feign;

import com.vd5.dcs2.Log;
import feign.Response;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;

import java.io.IOException;

/**
 * @author beou on 10/8/18 15:11
 */
public class DeviceErrorDecoder implements ErrorDecoder {
    final Decoder decoder;
    final ErrorDecoder defaultDecoder = new ErrorDecoder.Default();

    public DeviceErrorDecoder(Decoder decoder) {
        this.decoder = decoder;
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            return (Exception) decoder.decode(response, DeviceHubClientException.class);
        } catch (IOException fallbackToDefault) {
            return defaultDecoder.decode(methodKey, response);
        }
    }
}
