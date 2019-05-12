package com.git.service.yuanjunzi.datacenter.domain.repository;

import com.git.service.yuanjunzi.datacenter.domain.dto.DataCenterInputInfoDTO;
import com.git.service.yuanjunzi.datacenter.domain.dto.ErrorDTO;
import com.git.service.yuanjunzi.datacenter.infrastructure.dao.DataSource;
import com.git.service.yuanjunzi.datacenter.infrastructure.dao.ElasticSearchDAO;
import com.git.service.yuanjunzi.datacenter.infrastructure.dao.HBaseDAO;

import static com.git.service.yuanjunzi.datacenter.domain.dto.ErrorDTO.INNER_DATASOURCE_ERR;

/**
 * Created by yuanjunzi on 2019/4/26.
 * 普通查询数据
 */
public class CommonQueryParamResolutionStrategy extends DataCenterParamResolutionStrategy {
    @Override
    public ErrorDTO resolution(DataSource dataSource, DataCenterInputInfoDTO inputInfo) {
        if (dataSource instanceof ElasticSearchDAO) {
            prepareElasticSearchParam(inputInfo);
            dataSource.must(inputInfo.getMust()).mustNot(inputInfo.getMustNot()).should(inputInfo.getShould());
            return null;
        }
        if (dataSource instanceof HBaseDAO) {
            prepareHBaseParam(inputInfo);
            dataSource.must(inputInfo.getMust()).mustNot(inputInfo.getMustNot()).should(inputInfo.getShould());
            return null;
        }
        return INNER_DATASOURCE_ERR;
    }
}
