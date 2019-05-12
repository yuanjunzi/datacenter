package com.git.service.yuanjunzi.datacenter.infrastructure.listener;

import com.git.service.yuanjunzi.datacenter.domain.MonitorTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by yuanjunzi on 2019/3/27.
 * 监听业务监控指标模版
 */

@Slf4j
@Service("monitorTemplateListener")
public class MonitorTemplateListener extends AbstractTemplateListener {

    private static final String MCC_KEY = "monitorTemplateConfig";

    @PostConstruct
    public void init() {
        addObserver(new MonitorTemplate());
        templateConfig(client.getValue(MCC_KEY));
        client.addListener(MCC_KEY, (key, oldValue, newValue) -> templateConfig(newValue));
    }
}
