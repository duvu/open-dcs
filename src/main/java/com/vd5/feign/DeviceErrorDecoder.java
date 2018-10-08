package com.vd5.feign;

import com.vd5.dcs2.Log;
import feign.Response;
import feign.codec.ErrorDecoder;

/**
 * @author beou on 10/8/18 15:11
 */
public class DeviceErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String s, Response response) {
        Log.info("DeviceErrorDecoder# " + s);
        return null;
    }
}
