package com.git.service.yuanjunzi.datacenter.domain;

import com.git.service.yuanjunzi.datacenter.domain.dto.DataCenterInputInfoDTO;
import com.git.service.yuanjunzi.datacenter.infrastructure.dao.po.DataSourcePO;

import java.util.List;

/**
 * Created by yuanjunzi on 2018/10/29.
 * 将数据源的原始数据筛选拼装成需要的数据
 */
public interface UsableDataRepository {

    <T> DataSourcePO<List<T>> get();

    <T> void put(String tableName, T content);

    <T> DataSourcePO<List<T>> getWithCondition(DataCenterInputInfoDTO inputInfo);

    DataSourcePO<List<List<String>>> countWithCondition(DataCenterInputInfoDTO inputInfo);
}
