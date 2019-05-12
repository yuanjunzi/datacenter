package com.git.service.yuanjunzi.datacenter.domain;


import java.util.Map;

/**
 * Created by yuanjunzi on 2018/11/24.
 * 业务指标监控模版
 */
public class MonitorTemplate implements Observer {

    private volatile static Map<String, Map<String, Map<String, String>>> monitorTemplate;

    public static Map<String, Map<String, Map<String, String>>> getInstance() {
        return monitorTemplate;
    }

    @Override
    public void update(Map<String, Map<String, Map<String, String>>> message) {
        monitorTemplate = message;
    }
}
