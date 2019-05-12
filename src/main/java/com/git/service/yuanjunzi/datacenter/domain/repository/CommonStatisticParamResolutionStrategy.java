package com.git.service.yuanjunzi.datacenter.domain.repository;

import com.git.service.yuanjunzi.datacenter.constants.StatisticEnum;
import com.git.service.yuanjunzi.datacenter.domain.dto.DataCenterInputInfoDTO;
import com.git.service.yuanjunzi.datacenter.domain.dto.ErrorDTO;
import com.git.service.yuanjunzi.datacenter.infrastructure.dao.DataSource;
import com.git.service.yuanjunzi.datacenter.infrastructure.dao.ElasticSearchDAO;
import com.google.common.collect.ImmutableMap;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;

import java.util.Map;

import static com.git.service.yuanjunzi.datacenter.domain.converter.DataCenterInputInfoConverter.TIMELIMIT;
import static com.git.service.yuanjunzi.datacenter.domain.dto.ErrorDTO.INNER_DATASOURCE_ERR;
import static com.git.service.yuanjunzi.datacenter.domain.dto.ErrorDTO.PARAM_ERR;

/**
 * Created by yuanjunzi on 2019/4/26.
 * 普通统计
 */
public class CommonStatisticParamResolutionStrategy extends DataCenterParamResolutionStrategy {

    private static Map<String, Statistic> statisticMap;

    static {
        statisticMap = ImmutableMap.<String, Statistic>builder()
                .put(StatisticEnum.COUNT.name(), DataSource::cnt)
                .put(StatisticEnum.DISTINCT_COUNT.name(), DataSource::distinct_cnt)
                .build();
    }

    @Override
    public ErrorDTO resolution(DataSource dataSource, DataCenterInputInfoDTO inputInfo) {
        if (dataSource instanceof ElasticSearchDAO) {
            prepareElasticSearchParam(inputInfo);
            dataSource.must(inputInfo.getMust()).should(inputInfo.getShould()).mustNot(inputInfo.getMustNot());
            Statistic statistic = statisticMap.get(inputInfo.getType());
            if (statistic == null) {
                return PARAM_ERR;
            }
            statistic.calculations(dataSource, inputInfo.getBucket(), inputInfo.getMetrics(), calInterval(inputInfo), inputInfo.isNeedHistogram());
            return null;
        }
        return INNER_DATASOURCE_ERR;
    }

    interface Statistic {
        void calculations(DataSource source, String bucket, String metrics, DateHistogramInterval interval, boolean needHistogram);
    }

    private DateHistogramInterval calInterval(DataCenterInputInfoDTO inputInfo) {
        Object timeLimit = inputInfo.getMust().get(TIMELIMIT);
        if (timeLimit == null) {
            return DateHistogramInterval.DAY;
        }
        String timeLimitStr = timeLimit.toString();
        String[] timeRange = timeLimitStr.split(TIMELIMIT_SEPARATOR);
        if (timeRange.length != 2) {
            return DateHistogramInterval.DAY;
        }
        long start = Long.parseLong(timeRange[0]);
        long end = Long.parseLong(timeRange[1]);
        long intervalSecond = end - start;
        if (intervalSecond < 120 && intervalSecond >= 2) {
            return DateHistogramInterval.SECOND;
        }
        intervalSecond = intervalSecond / 60;
        if (intervalSecond < 120 && intervalSecond >= 2) {
            return DateHistogramInterval.MINUTE;
        }
        intervalSecond = intervalSecond / 60;
        if (intervalSecond < 120 && intervalSecond >= 2) {
            return DateHistogramInterval.HOUR;
        }
        return DateHistogramInterval.DAY;
    }
}
