package com.git.service.yuanjunzi.datacenter.application.task;

import com.git.service.yuanjunzi.datacenter.domain.factory.AbstractUsableDataFactory;
import com.git.service.yuanjunzi.datacenter.presentation.vo.DataCenterVisualDataVO;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by yuanjunzi on 2019/4/1.
 * 监控任务
 */

@Component("abstractMonitorTask")
public abstract class AbstractMonitorTask {

    @Resource(name = "shortTermUsableDataFactory")
    AbstractUsableDataFactory shortTermUsableDataFactory;

    abstract void monitor();

    abstract Map<String, DataCenterVisualDataVO> getMonitorMap();
}
