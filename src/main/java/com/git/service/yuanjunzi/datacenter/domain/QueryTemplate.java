package com.git.service.yuanjunzi.datacenter.domain;


import com.git.service.yuanjunzi.datacenter.constants.CriticalLogTermsEnum;
import com.git.service.yuanjunzi.datacenter.constants.StatisticEnum;
import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.git.service.yuanjunzi.datacenter.application.service.QueryTemplateTranslation.DENOMINATOR;
import static com.git.service.yuanjunzi.datacenter.application.service.QueryTemplateTranslation.NUMERATOR;
import static com.git.service.yuanjunzi.datacenter.domain.converter.DataCenterInputInfoConverter.*;

/**
 * Created by yuanjunzi on 2018/11/24.
 * 近期日志统计指标
 */
public class QueryTemplate implements Observer {

    private volatile static Map<String, Map<String, Map<String, String>>> queryTemplate;

    static {
        Map<String, ITemplate> initMap = ImmutableMap.<String, ITemplate>builder()
                .put("各类型操作的请求数", () -> {
                    Map<String, Map<String, String>> subQueryTemplate = new HashMap<>();
                    Map<String, String> statisticCondition = new HashMap<>();
                    statisticCondition.put(BUCKETTERM, CriticalLogTermsEnum.ACTION.getTerm());
                    statisticCondition.put(STATISTICTYPE, StatisticEnum.COUNT.name());
                    statisticCondition.put(STATISTICTERM, CriticalLogTermsEnum.SOURCE.getTerm());
                    subQueryTemplate.put(STATISTICCONDITION, statisticCondition);
                    return subQueryTemplate;
                })
                .put("各类型操作成功的请求数", () -> {
                    Map<String, Map<String, String>> subQueryTemplate = new HashMap<>();
                    Map<String, String> statisticCondition = new HashMap<>();
                    statisticCondition.put(BUCKETTERM, CriticalLogTermsEnum.ACTION.getTerm());
                    statisticCondition.put(STATISTICTYPE, StatisticEnum.COUNT.name());
                    statisticCondition.put(STATISTICTERM, CriticalLogTermsEnum.SOURCE.getTerm());
                    subQueryTemplate.put(STATISTICCONDITION, statisticCondition);
                    Map<String, String> queryCondition = new HashMap<>();
                    queryCondition.put(CriticalLogTermsEnum.STATUS.getTerm(), "success");
                    subQueryTemplate.put(MUSTCONDITION, queryCondition);
                    return subQueryTemplate;
                })
                .put("各类型操作的成功率", () -> {
                    Map<String, Map<String, String>> subQueryTemplate = new HashMap<>();
                    Map<String, String> rate = new HashMap<>();
                    rate.put(NUMERATOR, String.valueOf("各类型操作成功的请求数"));
                    rate.put(DENOMINATOR, String.valueOf("各类型操作的请求数"));
                    subQueryTemplate.put(RATECONDITION, rate);
                    return subQueryTemplate;
                })
                .put("各类型操作的设备数", () -> {
                    Map<String, Map<String, String>> subQueryTemplate = new HashMap<>();
                    Map<String, String> statisticCondition = new HashMap<>();
                    statisticCondition.put(BUCKETTERM, CriticalLogTermsEnum.ACTION.getTerm());
                    statisticCondition.put(STATISTICTYPE, StatisticEnum.DISTINCT_COUNT.name());
                    statisticCondition.put(STATISTICTERM, CriticalLogTermsEnum.UUID.getTerm());
                    subQueryTemplate.put(STATISTICCONDITION, statisticCondition);
                    return subQueryTemplate;
                })
                .put("各类型操作请求成功的设备数", () -> {
                    Map<String, Map<String, String>> subQueryTemplate = new HashMap<>();
                    Map<String, String> statisticCondition = new HashMap<>();
                    statisticCondition.put(BUCKETTERM, CriticalLogTermsEnum.ACTION.getTerm());
                    statisticCondition.put(STATISTICTYPE, StatisticEnum.DISTINCT_COUNT.name());
                    statisticCondition.put(STATISTICTERM, CriticalLogTermsEnum.UUID.getTerm());
                    subQueryTemplate.put(STATISTICCONDITION, statisticCondition);
                    Map<String, String> queryCondition = new HashMap<>();
                    queryCondition.put(CriticalLogTermsEnum.STATUS.getTerm(), "success");
                    subQueryTemplate.put(MUSTCONDITION, queryCondition);
                    return subQueryTemplate;
                })
                .put("各类型操作设备的请求成功率", () -> {
                    Map<String, Map<String, String>> subQueryTemplate = new HashMap<>();
                    Map<String, String> rate = new HashMap<>();
                    rate.put(NUMERATOR, String.valueOf("各类型操作请求成功的设备数"));
                    rate.put(DENOMINATOR, String.valueOf("各类型操作的设备数"));
                    subQueryTemplate.put(RATECONDITION, rate);
                    return subQueryTemplate;
                })
                .put("各类型操作的请求用户数", () -> {
                    Map<String, Map<String, String>> subQueryTemplate = new HashMap<>();
                    Map<String, String> statisticCondition = new HashMap<>();
                    statisticCondition.put(BUCKETTERM, CriticalLogTermsEnum.ACTION.getTerm());
                    statisticCondition.put(STATISTICTYPE, StatisticEnum.DISTINCT_COUNT.name());
                    statisticCondition.put(STATISTICTERM, CriticalLogTermsEnum.USERID.getTerm());
                    subQueryTemplate.put(STATISTICCONDITION, statisticCondition);
                    return subQueryTemplate;
                })
                .build();
        Map<String, Map<String, Map<String, String>>> map = new LinkedHashMap<>();
        for (Map.Entry<String, ITemplate> entry : initMap.entrySet()) {
            map.put(entry.getKey(), entry.getValue().get());
        }
        queryTemplate = map;
    }

    public static Map<String, Map<String, Map<String, String>>> getInstance() {
        return queryTemplate;
    }

    @Override
    public void update(Map<String, Map<String, Map<String, String>>> message) {
        queryTemplate = message;
    }
}
