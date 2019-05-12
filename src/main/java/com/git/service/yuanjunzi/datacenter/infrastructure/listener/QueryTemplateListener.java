package com.git.service.yuanjunzi.datacenter.infrastructure.listener;

import com.git.service.yuanjunzi.datacenter.domain.QueryTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by yuanjunzi on 2019/2/15.
 * 监听近期数据统计模版
 */

@Service("queryTemplateListener")
public class QueryTemplateListener extends AbstractTemplateListener {

    private static final String MCC_KEY = "queryTemplateConfig";

    @PostConstruct
    public void init() {
        addObserver(new QueryTemplate());
        templateConfig(client.getValue(MCC_KEY));
        client.addListener(MCC_KEY, (key, oldValue, newValue) -> templateConfig(newValue));
    }
}
