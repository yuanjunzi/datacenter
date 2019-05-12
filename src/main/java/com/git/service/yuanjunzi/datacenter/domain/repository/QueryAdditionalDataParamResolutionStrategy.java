package com.git.service.yuanjunzi.datacenter.domain.repository;

import com.git.service.yuanjunzi.datacenter.constants.ActionEnum;
import com.git.service.yuanjunzi.datacenter.domain.dto.DataCenterInputInfoDTO;
import com.git.service.yuanjunzi.datacenter.domain.dto.ErrorDTO;
import com.git.service.yuanjunzi.datacenter.infrastructure.dao.DataSource;
import com.git.service.yuanjunzi.datacenter.infrastructure.dao.HBaseDAO;
import com.git.service.yuanjunzi.datacenter.infrastructure.utils.TimeUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static com.git.service.yuanjunzi.datacenter.constants.CriticalLogTermsEnum.ACTION;
import static com.git.service.yuanjunzi.datacenter.constants.CriticalLogTermsEnum.TIME;
import static com.git.service.yuanjunzi.datacenter.constants.CriticalLogTermsEnum.USERID;
import static com.git.service.yuanjunzi.datacenter.domain.dto.ErrorDTO.INNER_DATASOURCE_ERR;
import static com.git.service.yuanjunzi.datacenter.infrastructure.dao.HBaseDAO.EXACT_QUERY;
import static com.git.service.yuanjunzi.datacenter.infrastructure.dao.HBaseDAO.QUERY_TABLE;


/**
 * Created by yuanjunzi on 2019/4/26.
 * 查询额外日志数据，目前仅存在于HBase中
 */
public class QueryAdditionalDataParamResolutionStrategy extends DataCenterParamResolutionStrategy {
    @Override
    public ErrorDTO resolution(DataSource dataSource, DataCenterInputInfoDTO inputInfo) {
        if (dataSource instanceof HBaseDAO) {
            String action = ActionEnum.getNameToActionMap().get(String.valueOf(inputInfo.getMust().get(ACTION.getTerm())));
            String time = String.valueOf(inputInfo.getMust().get(TIME.getTerm()));
            String userId = String.valueOf(inputInfo.getMust().get(USERID.getTerm()));
            String tableName = String.valueOf(inputInfo.getMust().get(QUERY_TABLE));
            if (StringUtils.isNotBlank(action) && !"null".equals(time) && !"null".equals(userId)) {
                Map<String, Object> map = new HashMap<>();
                String key = DigestUtils.md5Hex(userId) + "_" + DigestUtils.md5Hex(action) + "_" + TimeUtils.stringToLong(time, TimeUtils.DEFAULT_TIME_FORMAT);
                map.put("rowKey", key);
                map.put(QUERY_TABLE, tableName);
                map.put(EXACT_QUERY, true);
                inputInfo.setMust(map);
            }
            prepareHBaseParam(inputInfo);
            dataSource.must(inputInfo.getMust()).mustNot(inputInfo.getMustNot()).should(inputInfo.getShould());
            return null;
        }
        return INNER_DATASOURCE_ERR;
    }
}
