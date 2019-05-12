package com.git.service.yuanjunzi.datacenter.domain.factory;

import com.git.service.yuanjunzi.datacenter.domain.AssemblingDataRepository;
import com.git.service.yuanjunzi.datacenter.domain.UsableDataRepository;
import com.git.service.yuanjunzi.datacenter.infrastructure.dao.ElasticSearchDAO;
import org.springframework.stereotype.Service;

/**
 * Created by yuanjunzi on 2019/3/25.
 * 近期数据源工厂
 */

@Service("shortTermUsableDataFactory")
public class ShortTermUsableDataFactory extends AbstractUsableDataFactory {

    private static final UsableDataRepository USABLE_DATA_REPOSITORY = null;

    @Override
    public UsableDataRepository createUsableData() {
        return new AssemblingDataRepository(new ElasticSearchDAO(clusterName, clusterAddress));
    }

    public static UsableDataRepository getUsableDataDefault() {
        return USABLE_DATA_REPOSITORY;
    }
}
