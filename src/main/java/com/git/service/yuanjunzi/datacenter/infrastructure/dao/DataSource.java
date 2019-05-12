package com.git.service.yuanjunzi.datacenter.infrastructure.dao;

import com.git.service.yuanjunzi.datacenter.infrastructure.dao.po.DataSourcePO;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;

import java.util.Map;

/**
 * Created by yuanjunzi on 2018/10/29.
 * 数据源接口
 */

public interface DataSource {

    <T> DataSourcePO<T> get();

    <T> boolean insert(String tableName, DataSourcePO<T> content);

    DataSource must(Map<String, Object> addCondition);

    DataSource should(Map<String, Object> addCondition);

    DataSource mustNot(Map<String, Object> addCondition);

    DataSource max(String bucket, String metrics, DateHistogramInterval interval, boolean needHistogram);

    DataSource min(String bucket, String metrics, DateHistogramInterval interval, boolean needHistogram);

    DataSource sum(String bucket, String metrics, DateHistogramInterval interval, boolean needHistogram);

    DataSource avg(String bucket, String metrics, DateHistogramInterval interval, boolean needHistogram);

    DataSource cnt(String bucket, String metrics, DateHistogramInterval interval, boolean needHistogram);

    DataSource distinct_cnt(String bucket, String metrics, DateHistogramInterval interval, boolean needHistogram);

    DataSource clear();
}
