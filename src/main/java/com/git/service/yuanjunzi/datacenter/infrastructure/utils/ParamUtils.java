package com.git.service.yuanjunzi.datacenter.infrastructure.utils;

/**
 * @author haojiakang
 * @date 2019/3/4 4:12 PM
 */
public class ParamUtils {

    public static int replaceIfNull(Integer source, int toReplace) {
        return source == null ? toReplace : source;
    }
}
