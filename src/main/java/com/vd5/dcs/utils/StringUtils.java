package com.vd5.dcs.utils;

/**
 * @author beou on 8/27/17 03:48
 * @version 1.0
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {
//    public static boolean isEmpty(CharSequence s) {
//        return org.apache.commons.lang3.StringUtils.isEmpty(s);
//    }
//
//    public static boolean isNotEmpty(CharSequence s) {
//        return org.apache.commons.lang3.StringUtils.isNotEmpty(s);
//    }

    public static String toLowerCase(String s) {
        return org.apache.commons.lang3.StringUtils.lowerCase(s);
    }

    public static String toUpperCase(String s) {
        return org.apache.commons.lang3.StringUtils.upperCase(s);
    }
}
