package com.git.service.yuanjunzi.datacenter.infrastructure.utils;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

public class JSONUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(JSONUtils.class);

    private static final Gson gson = new Gson();

    public static <T> T fromJson(String json, Type typeOfT) {
        try {
            return gson.fromJson(json, typeOfT);
        } catch (Exception e) {
            LOGGER.info("fromJson fail, json={}, type={}", json, typeOfT);
            return null;
        }
    }

    public static String toJson(Object object) {
        return gson.toJson(object);
    }
}
