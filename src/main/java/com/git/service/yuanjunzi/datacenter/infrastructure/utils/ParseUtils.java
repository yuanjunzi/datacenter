package com.git.service.yuanjunzi.datacenter.infrastructure.utils;

/**
 * 解析工具
 *
 * @author liwei07
 * @version 1.0
 * @created 2015年05月01日
 */
public class ParseUtils {

    public static int parseInt(String str, int defaultValue) {
        try {
            int i = Integer.parseInt(str);
            return i;
        } catch (Exception e) {
        }
        return defaultValue;
    }

    public static long parseLong(String str, long defaultValue) {
        try {
            long l = Long.parseLong(str);
            return l;
        } catch (Exception e) {
        }
        return defaultValue;
    }

    public static boolean parseBoolean(String str) {
        boolean b = Boolean.parseBoolean(str);
        return b;
    }

    public static double parseDouble(String str, double defaultValue) {
        try {
            double d = Double.parseDouble(str);
            return d;
        } catch (Exception e) {
        }
        return defaultValue;
    }

    public static float parseFloat(String str, float defaultValue) {
        try {
            float f = Float.parseFloat(str);
            return f;
        } catch (Exception e) {
        }
        return defaultValue;
    }

    public static int parseIntByObject(Object obj, int defaultValue) {
        if (obj instanceof Integer) {
            return ((Integer) obj).intValue();
        }
        return defaultValue;
    }
}
