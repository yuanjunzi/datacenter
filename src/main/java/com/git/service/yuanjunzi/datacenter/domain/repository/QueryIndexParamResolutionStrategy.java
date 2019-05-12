package com.git.service.yuanjunzi.datacenter.domain.repository;

import com.git.service.yuanjunzi.datacenter.domain.dto.DataCenterInputInfoDTO;
import com.git.service.yuanjunzi.datacenter.domain.dto.ErrorDTO;
import com.git.service.yuanjunzi.datacenter.infrastructure.dao.DataSource;
import com.git.service.yuanjunzi.datacenter.infrastructure.dao.ElasticSearchDAO;

import static com.git.service.yuanjunzi.datacenter.domain.dto.ErrorDTO.INNER_DATASOURCE_ERR;
import static com.git.service.yuanjunzi.datacenter.infrastructure.dao.ElasticSearchDAO.QUERY_INDEX;

/**
 * Created by yuanjunzi on 2019/4/26.
 * 查询索引数据
 */
public class QueryIndexParamResolutionStrategy extends DataCenterParamResolutionStrategy {
    @Override
    public ErrorDTO resolution(DataSource dataSource, DataCenterInputInfoDTO inputInfo) {
        if (dataSource instanceof ElasticSearchDAO) {
            DataCenterInputInfoDTO inputInfo2 = inputInfo.copy();
            inputInfo2.addMust(QUERY_INDEX, true);
            prepareElasticSearchParam(inputInfo2);
            dataSource.must(inputInfo2.getMust()).mustNot(inputInfo2.getMustNot()).should(inputInfo2.getShould());
            return null;
        }
        return INNER_DATASOURCE_ERR;
    }
}
