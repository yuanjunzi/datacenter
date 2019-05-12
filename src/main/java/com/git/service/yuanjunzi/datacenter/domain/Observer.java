package com.git.service.yuanjunzi.datacenter.domain;

import java.util.Map;

/**
 * Created by yuanjunzi on 2019/3/27.
 * 观察者
 */
public interface Observer {
    void update(Map<String, Map<String, Map<String, String>>> message);
}
