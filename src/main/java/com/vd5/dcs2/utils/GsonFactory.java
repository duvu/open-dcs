package com.vd5.dcs2.utils;

import com.google.gson.Gson;

/**
 * @author beou on 10/22/18 21:03
 */
public class GsonFactory {
    private static Gson gson;
    public static Gson get() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }
}
