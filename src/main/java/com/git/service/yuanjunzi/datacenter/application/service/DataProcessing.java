package com.git.service.yuanjunzi.datacenter.application.service;

import com.git.service.yuanjunzi.datacenter.domain.UsableDataRepository;
import com.git.service.yuanjunzi.datacenter.presentation.vo.DataCenterInputInfoVO;
import com.git.service.yuanjunzi.datacenter.presentation.vo.DataCenterVisualDataVO;

/**
 * Created by yuanjunzi on 2018/12/18.
 * 数据查询外观
 */
public interface DataProcessing {
    DataCenterVisualDataVO specificTreatment(DataCenterInputInfoVO inputInfo, UsableDataRepository... usableDatumRepositories);
}
