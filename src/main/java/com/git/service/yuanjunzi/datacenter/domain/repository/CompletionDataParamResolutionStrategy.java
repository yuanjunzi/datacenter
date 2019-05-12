package com.git.service.yuanjunzi.datacenter.domain.repository;

import com.git.service.yuanjunzi.datacenter.domain.dto.DataCenterInputInfoDTO;
import com.git.service.yuanjunzi.datacenter.domain.dto.ErrorDTO;
import com.git.service.yuanjunzi.datacenter.infrastructure.dao.DataSource;
import com.git.service.yuanjunzi.datacenter.infrastructure.dao.ElasticSearchDAO;

import static com.git.service.yuanjunzi.datacenter.domain.converter.DataCenterInputInfoConverter.ACTIONNAME;
import static com.git.service.yuanjunzi.datacenter.domain.dto.ErrorDTO.INNER_DATASOURCE_ERR;

/**
 * Created by yuanjunzi on 2019/4/26.
 * 补全数据,暂时只支持ES
 */
public class CompletionDataParamResolutionStrategy extends DataCenterParamResolutionStrategy {
    @Override
    public ErrorDTO resolution(DataSource dataSource, DataCenterInputInfoDTO inputInfo) {
        if (dataSource instanceof ElasticSearchDAO) {
            inputInfo.addMust(COMPLETING_DATA_TODAY, "true");
            //对于ES补全数据，action条件应该放在or位置
            Object actionCondition = inputInfo.getMust().get(ACTIONNAME);
            if (actionCondition != null) {
                inputInfo.addShould(ACTIONNAME, actionCondition);
                inputInfo.getMust().remove(ACTIONNAME);
            }
            prepareElasticSearchParam(inputInfo);
            dataSource.must(inputInfo.getMust()).mustNot(inputInfo.getMustNot()).should(inputInfo.getShould());
            return null;
        }
        return INNER_DATASOURCE_ERR;
    }
}
