package com.git.service.yuanjunzi.datacenter.constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanjunzi on 2019/1/23.
 */
public enum QueryDimensionEnum {
    USERID,
    MOBILE;

    private static volatile List<String> list;

    public static List<String> getQueryDimension() {
        if (list == null) {
            synchronized (QueryDimensionEnum.class) {
                if (list == null) {
                    List<String> tempList = new ArrayList<>();
                    for (QueryDimensionEnum item : QueryDimensionEnum.values()) {
                        tempList.add(item.name());
                    }
                    list = tempList;
                }
            }
        }
        return list;
    }
}
