package com.git.service.yuanjunzi.datacenter.domain.converter;

import com.git.service.yuanjunzi.datacenter.domain.dto.DataCenterInputInfoDTO;
import com.git.service.yuanjunzi.datacenter.domain.dto.QueryConditionDTO;
import com.git.service.yuanjunzi.datacenter.infrastructure.utils.JSONUtils;
import com.git.service.yuanjunzi.datacenter.presentation.vo.DataCenterInputInfoVO;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by yuanjunzi on 2019/4/24.
 * 前端输入转换为可在后台传输的数据对象
 */
public class DataCenterInputInfoConverter extends Converter<DataCenterInputInfoVO, DataCenterInputInfoDTO> {

    public static final String OPERATIONTYPE = "operationType";

    public static final String QUERYTEMPLATE = "queryTemplate";

    public static final String MONITORTEMPLATE = "monitorTemplate";

    public static final String MUSTCONDITION = "mustCondition";

    public static final String MUSTNOTCONDITION = "mustNotCondition";

    public static final String SHOULDCONDITION = "shouldCondition";

    public static final String STATISTICCONDITION = "statisticCondition";

    public static final String RATECONDITION = "rateCondition";

    public static final String BUCKETTERM = "bucketTerm";

    public static final String TIMELIMIT = "timeLimit";

    public static final String ACTIONNAME = "actionName";

    public static final String STATISTICTYPE = "statisticType";

    public static final String STATISTICTERM = "statisticTerm";

    public static final String QUERYDIMENSION = "queryDimension";

    public static final String SEPARATOR = "_";

    public DataCenterInputInfoConverter() {
        super(new DataCenterInputInfoVOFunction(), new DataCenterInputInfoDTOFunction());
    }

    static class DataCenterInputInfoVOFunction implements Function<DataCenterInputInfoVO, DataCenterInputInfoDTO> {

        @Override
        public DataCenterInputInfoDTO apply(DataCenterInputInfoVO vo) {
            DataCenterInputInfoDTO dto = new DataCenterInputInfoDTO();
            dto.addMust(getQueryCondition(vo.getRequestParam(), MUSTCONDITION, vo.getPosition()));
            dto.addMustNot(getQueryCondition(vo.getRequestParam(), MUSTNOTCONDITION, vo.getPosition()));
            dto.addShould(getQueryCondition(vo.getRequestParam(), SHOULDCONDITION, vo.getPosition()));
            dto.setBucket(vo.getRequestParam().get(BUCKETTERM + vo.getPosition()));
            dto.setType(vo.getRequestParam().get(STATISTICTYPE + vo.getPosition()));
            dto.setMetrics(vo.getRequestParam().get(STATISTICTERM + vo.getPosition()));
            return dto;
        }

        private Map<String, Object> getQueryCondition(Map<String, String> req, String conditionName, String suffix) {
            List<QueryConditionDTO> queryConditionList = null;
            String json = req.get(conditionName + suffix);
            if (!StringUtils.isBlank(json)) {
                queryConditionList = JSONUtils.fromJson(json, new TypeToken<List<QueryConditionDTO>>() {
                }.getType());
            }
            Map<String, Object> condition = new HashMap<>();
            if (queryConditionList != null && !queryConditionList.isEmpty()) {
                int inc = 0;
                for (QueryConditionDTO item : queryConditionList) {
                    if (condition.get(item.getTerm()) != null) {
                        condition.put(item.getTerm() + SEPARATOR + inc++, item.getValue());
                        continue;
                    }
                    condition.put(item.getTerm(), item.getValue());
                }
            }
            return condition;
        }
    }

    static class DataCenterInputInfoDTOFunction implements Function<DataCenterInputInfoDTO, DataCenterInputInfoVO> {

        @Override
        public DataCenterInputInfoVO apply(DataCenterInputInfoDTO dataCenterInputInfoDTO) {
            return new DataCenterInputInfoVO();
        }
    }
}
