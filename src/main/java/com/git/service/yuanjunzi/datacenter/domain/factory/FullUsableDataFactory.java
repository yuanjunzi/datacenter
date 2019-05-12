package com.git.service.yuanjunzi.datacenter.domain.factory;

import com.git.service.yuanjunzi.datacenter.domain.AssemblingDataRepository;
import com.git.service.yuanjunzi.datacenter.domain.UsableDataRepository;
import com.git.service.yuanjunzi.datacenter.infrastructure.dao.ElasticSearchDAO;
import com.git.service.yuanjunzi.datacenter.infrastructure.dao.HBaseDAO;
import org.springframework.stereotype.Service;

/**
 * Created by yuanjunzi on 2019/3/25.
 * 全量数据源工厂
 */

@Service("fullUsableDataFactory")
public class FullUsableDataFactory extends AbstractUsableDataFactory {


    private static final UsableDataRepository USABLE_DATA_REPOSITORY = new AssemblingDataRepository(new HBaseDAO());

    @Override
    public UsableDataRepository createUsableData() {
        return new AssemblingDataRepository(new HBaseDAO()
                , new ElasticSearchDAO(clusterName, clusterAddress), new ElasticSearchDAO(clusterName, clusterAddress));
    }

    public static UsableDataRepository getUsableDataDefault() {
        return USABLE_DATA_REPOSITORY;
    }
}