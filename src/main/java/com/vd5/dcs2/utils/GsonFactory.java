package com.vd5.dcs2.utils;

import com.google.gson.Gson;
import sun.plugin2.util.PojoUtil;

/**
 * @author beou on 10/22/18 21:03
 */
public class GsonFactory {
    private static Gson gson;
    public static Gson getGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }
}
