package com.vd5.dcs.utils;

import java.util.Date;

/**
 * @author beou on 9/2/18 08:54
 */
public class TimeUtils {

    public static long distanceToNow(long timestampInPast) {
        return (new Date()).getTime() - timestampInPast;
    }

    public static long getCurrentTimestamp() {
        return new Date().getTime();
    }

}
