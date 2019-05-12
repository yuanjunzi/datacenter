package com.git.service.yuanjunzi.datacenter.application.service;

import com.git.service.yuanjunzi.datacenter.domain.dto.QueryConditionDTO;
import com.git.service.yuanjunzi.datacenter.infrastructure.utils.JSONUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.git.service.yuanjunzi.datacenter.application.service.DataProcessFacade.QUERY_DETAIL_OR_STATISTIC;
import static com.git.service.yuanjunzi.datacenter.application.service.DataProcessFacade.STATISTIC_AND_CALCULATE_RATIO;
import static com.git.service.yuanjunzi.datacenter.domain.converter.DataCenterInputInfoConverter.*;

/**
 * Created by yuanjunzi on 2019/4/24.
 * 查询模版翻译策略
 */
@Service("queryTemplateTranslation")
public class QueryTemplateTranslation implements TranslationStrategy {

    public static final String NUMERATOR = "numerator";

    public static final String DENOMINATOR = "denominator";

    @Override
    public void translate(Map<String, String> req, Map<String, Map<String, Map<String, String>>> templateMap, String queryTemplate) {
        if (StringUtils.isNotBlank(queryTemplate)) {
            fillUpMap(req, templateMap, queryTemplate);
            req.remove(QUERYTEMPLATE);
        }
    }

    private void fillUpMap(Map<String, String> req, Map<String, Map<String, Map<String, String>>> templateMap, String queryTemplate) {
        fillUpMap(req, templateMap, queryTemplate, "");
    }

    private void fillUpMap(Map<String, String> req, Map<String, Map<String, Map<String, String>>> templateMap, String queryTemplate, String suffix) {
        Map<String, Map<String, String>> template = templateMap.get(queryTemplate);
        if (template == null || template.isEmpty()) {
            return;
        }
        Map<String, String> rateCondition = template.get(RATECONDITION);
        if (rateCondition == null || rateCondition.isEmpty()) {
            extractCondition(req, template, MUSTCONDITION, suffix);
            extractCondition(req, template, MUSTNOTCONDITION, suffix);
            extractCondition(req, template, SHOULDCONDITION, suffix);
            Map<String, String> statisticMap = template.get(STATISTICCONDITION);
            req.put(BUCKETTERM + suffix, statisticMap.get(BUCKETTERM));
            req.put(STATISTICTYPE + suffix, statisticMap.get(STATISTICTYPE));
            req.put(STATISTICTERM + suffix, statisticMap.get(STATISTICTERM));
            req.put(OPERATIONTYPE + suffix, String.valueOf(QUERY_DETAIL_OR_STATISTIC));
            return;
        }
        fillUpMap(req, templateMap, rateCondition.get(NUMERATOR), NUMERATOR);
        fillUpMap(req, templateMap, rateCondition.get(DENOMINATOR), DENOMINATOR);
        req.put(OPERATIONTYPE, String.valueOf(STATISTIC_AND_CALCULATE_RATIO));
    }

    private void extractCondition(Map<String, String> req, Map<String, Map<String, String>> template, String conditionName, String suffix) {
        Map<String, String> queryCondition = template.get(conditionName);
        String json = req.get(conditionName);
        if ((queryCondition != null && !queryCondition.isEmpty()) || StringUtils.isNotBlank(json)) {
            List<QueryConditionDTO> queryConditionDTOList = new ArrayList<>();
            if (StringUtils.isNotBlank(json)) {
                List<QueryConditionDTO> queryConditionList = JSONUtils.fromJson(json, new TypeToken<List<QueryConditionDTO>>() {
                }.getType());
                if (queryConditionList != null && !queryConditionList.isEmpty()) {
                    queryConditionDTOList.addAll(queryConditionList);
                }
            }
            if (queryCondition != null && !queryCondition.isEmpty()) {
                for (Map.Entry<String, String> item : queryCondition.entrySet()) {
                    QueryConditionDTO queryConditionDTO = new QueryConditionDTO();
                    queryConditionDTO.setTerm(item.getKey());
                    queryConditionDTO.setValue(item.getValue());
                    queryConditionDTOList.add(queryConditionDTO);
                }
            }
            req.put(conditionName + suffix, JSONUtils.toJson(queryConditionDTOList));
        }
    }
}
