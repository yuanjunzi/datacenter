package com.git.service.yuanjunzi.datacenter.domain;

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by yuanjunzi on 2019/5/9.
 * 报警配置模版
 */
public class AlarmTemplate implements Observer {

    private final static String SATISFY_ALL_OF_THEM = "and";

    private final static String SATISFY_ONE_OF_THEM = "or";

    private final static String TRAFFIC_THRESHOLD = "trafficThreshold";

    private final static String TRAFFIC_COMPARE_TO_LAST_MIN = "trafficCompareToLastMin";

    private final static String TRAFFIC_COMPARE_TO_LAST_MIN_PERCENTAGE = "trafficCompareToLastMinPercentage";

    private final static String AVERAGE_TRAFFIC_COMPARE_TO_LAST_WEEK = "averageTrafficCompareToLastWeek";

    private final static String AVERAGE_TRAFFIC_COMPARE_TO_LAST_WEEK_PERCENTAGE = "averageTrafficCompareToLastWeekPercentage";

    private final static String CONDITION_SEPARATOR = ",";

    private volatile static Map<String, Map<String, Map<String, String>>> alramTemplate;

    static {
        Map<String, ITemplate> initMap = ImmutableMap.<String, ITemplate>builder()
                .put("小程序登录的请求数", () -> {
                    Map<String, Map<String, String>> subAlarmTemplate = new HashMap<>();
                    Map<String, String> and = new HashMap<>();
                    and.put(TRAFFIC_COMPARE_TO_LAST_MIN, "100");
                    subAlarmTemplate.put(SATISFY_ALL_OF_THEM, and);
                    Map<String, String> or = new HashMap<>();
                    or.put(TRAFFIC_THRESHOLD, "3" + CONDITION_SEPARATOR + "-100" + CONDITION_SEPARATOR + "100");
                    or.put(TRAFFIC_COMPARE_TO_LAST_MIN_PERCENTAGE, "3" + CONDITION_SEPARATOR + "-0.1" + CONDITION_SEPARATOR + "0.1");
                    or.put(AVERAGE_TRAFFIC_COMPARE_TO_LAST_WEEK, "-100" + CONDITION_SEPARATOR + "100");
                    or.put(AVERAGE_TRAFFIC_COMPARE_TO_LAST_WEEK_PERCENTAGE, "-0.5" + CONDITION_SEPARATOR + "0.5");
                    subAlarmTemplate.put(SATISFY_ONE_OF_THEM, or);
                    return subAlarmTemplate;
                })
                .build();
        Map<String, Map<String, Map<String, String>>> map = new LinkedHashMap<>();
        for (Map.Entry<String, ITemplate> entry : initMap.entrySet()) {
            map.put(entry.getKey(), entry.getValue().get());
        }
        alramTemplate = map;
    }

    public static Map<String, Map<String, Map<String, String>>> getInstance() {
        return alramTemplate;
    }

    @Override
    public void update(Map<String, Map<String, Map<String, String>>> message) {
        alramTemplate = message;
    }
}
