package com.git.service.yuanjunzi.datacenter.infrastructure.utils;

import java.util.regex.Pattern;

/**
 * Created by wangshenxiang on 2017/4/27.
 */
public class CoreUtils {

    private static final Pattern MOBILE_NUMBER_PATTERN = Pattern.compile("(^1(3|4|5|7|8)[0-9]{9}$)|(^0060[0-9]{9}$)");

    public static boolean checkMobileNumber(String mobile) {
        return MOBILE_NUMBER_PATTERN.matcher(mobile).matches();
    }

    public static String getMaskMobile(String mobile) {
        if (checkMobileNumber(mobile)) {
            return mobile.substring(0, 3) + "****" + mobile.substring(7, 11);
        } else {
            return mobile;
        }
    }
}
