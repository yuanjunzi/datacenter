package com.git.service.yuanjunzi.datacenter.domain.factory;

import com.git.service.yuanjunzi.datacenter.domain.UsableDataRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by yuanjunzi on 2019/3/21.
 * 可用数据源工厂
 */

@Service
public abstract class AbstractUsableDataFactory {

    @Value("${es.clusterName}")
    protected String clusterName;

    @Value("${es.clusterAddress}")
    protected String clusterAddress;

    public abstract UsableDataRepository createUsableData();
}
