package com.git.service.yuanjunzi.datacenter.infrastructure.listener;

import com.git.service.yuanjunzi.datacenter.domain.Observer;
import com.git.service.yuanjunzi.datacenter.infrastructure.utils.JSONUtils;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yuanjunzi on 2019/3/27.
 * 虚拟模版监听者，提供公共的模版监听参数
 */
@Slf4j
@Service
public abstract class AbstractTemplateListener implements Observable<Observer> {

    private List<Observer> observers = new ArrayList<>();

    @Resource(name = "configClient")
    ConfigClient client;

    public abstract void init();

    void templateConfig(String configStr) {
        log.info("templateConfig configStr={}", configStr);
        if (StringUtils.isBlank(configStr)) {
            return;
        }
        Map<String, Map<String, Map<String, String>>> templateConfigMap = JSONUtils.fromJson(
                configStr, new TypeToken<Map<String, Map<String, Map<String, String>>>>() {
                }.getType());
        if (templateConfigMap == null || templateConfigMap.isEmpty()) {
            log.error("templateConfig change filed! templateConfigMap=null! newValue={}", configStr);
            return;
        }
        notifyObserver(templateConfigMap);
        log.info("templateConfig succss. result={}", templateConfigMap);
    }

    @Override
    public void notifyObserver(Map<String, Map<String, Map<String, String>>> message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }

    @Override
    public boolean addObserver(Observer observer) {
        this.observers.add(observer);
        return true;
    }

    @Override
    public boolean removeObserver(Observer observer) {
        this.observers.remove(observer);
        return true;
    }
}
