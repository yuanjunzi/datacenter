package com.git.service.yuanjunzi.datacenter.constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanjunzi on 2018/11/2.
 */
public enum StatisticEnum {
    COUNT, DISTINCT_COUNT;

    private volatile static List<String> statisticTypeList;

    public static List<String> toList() {
        if (statisticTypeList == null) {
            synchronized (StatisticEnum.class) {
                if (statisticTypeList == null) {
                    List<String> list = new ArrayList<>();
                    for (StatisticEnum item : StatisticEnum.values()) {
                        list.add(item.name());
                    }
                    statisticTypeList = list;
                }
            }
        }
        return statisticTypeList;
    }
}
