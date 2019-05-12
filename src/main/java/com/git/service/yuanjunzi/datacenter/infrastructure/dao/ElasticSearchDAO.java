package com.git.service.yuanjunzi.datacenter.infrastructure.dao;

import com.git.service.yuanjunzi.datacenter.constants.ActionEnum;
import com.git.service.yuanjunzi.datacenter.constants.CriticalLogTermsEnum;
import com.git.service.yuanjunzi.datacenter.domain.dto.CriticalLogTermsDTO;
import com.git.service.yuanjunzi.datacenter.domain.dto.ExtraLogTermsDTO;
import com.git.service.yuanjunzi.datacenter.domain.factory.AbstractUsableDataFactory;
import com.git.service.yuanjunzi.datacenter.infrastructure.dao.po.DataSourcePO;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.InternalDateHistogram;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.cardinality.Cardinality;
import org.elasticsearch.search.aggregations.metrics.cardinality.CardinalityAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.valuecount.InternalValueCount;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCountAggregationBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.joda.time.DateTimeZone;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import static com.git.service.yuanjunzi.datacenter.application.service.DataProcessFacade.ONLY_QUERY;
import static com.git.service.yuanjunzi.datacenter.application.service.DataProcessFacade.QUERY_DETAIL_OR_STATISTIC;
import static com.git.service.yuanjunzi.datacenter.constants.CriticalLogTermsEnum.ACTION;
import static com.git.service.yuanjunzi.datacenter.constants.CriticalLogTermsEnum.TIME;
import static com.git.service.yuanjunzi.datacenter.domain.converter.DataCenterInputInfoConverter.*;
import static com.git.service.yuanjunzi.datacenter.domain.repository.DataCenterParamResolutionStrategy.ACTION_SEPARATOR;
import static com.git.service.yuanjunzi.datacenter.domain.repository.DataCenterParamResolutionStrategy.TIMELIMIT_SEPARATOR;
import static com.git.service.yuanjunzi.datacenter.infrastructure.dao.HBaseDAO.DETAILS;
import static com.git.service.yuanjunzi.datacenter.infrastructure.dao.HBaseDAO.QUERY_TABLE;
import static com.git.service.yuanjunzi.datacenter.infrastructure.utils.TimeUtils.*;

/**
 * Created by yuanjunzi on 2018/10/29.
 * 用于通过ES查询数据
 * 不应直接实例化。建议通过 {@link AbstractUsableDataFactory} 完成实例化
 */
@Slf4j
public class ElasticSearchDAO implements DataSource {

    public static final String INDEX_FOREIGNKEY = "foreignKey";

    public static final String QUERY_INDEX = "queryIndex";

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ssZ";

    private volatile static Client client;

    private static final String DEFAULT_INDEX = "log.passport_all";

    private static final String MOBILE_INDEX = "app.usercenter_logquery_mobileindex_all";

    private static final String DEFAULT_TYPE = "logs";

    private static final String AGGREGATION_NAME = "agg";

    private static final String DATE_BUCKET_NAME = "by_day";

    private static final String METRICS_BUCKET_NAME = "by_bucket";

    private static final String CRITICAL_INFO = "criticalInfo";

    private static final String EXTRA_INFO = "extraInfo";

    private static final String INDEX = "mobileIndex";

    private static final String TIME_ZONE = "+08:00";

    private static final int MAX_DOC_NUM = 10000;

    private static final int MAX_AGG_NUM = 50;

    private static final int ESPORT = 9300;

    private static Map<String, Value> aggregationValue;

    private BoolQueryBuilder queryCondition;

    private AggregationBuilder aggregation;

    private SearchRequestBuilder response;

    private String resultType;

    static {
        aggregationValue = ImmutableMap.<String, Value>builder()
                .put(CardinalityAggregationBuilder.NAME, (Aggregations aggregations) -> {
                    Cardinality cardinality = aggregations.get(AGGREGATION_NAME);
                    return cardinality.getValue();
                })
                .put(ValueCountAggregationBuilder.NAME, (Aggregations aggregations) -> {
                    InternalValueCount valueCount = aggregations.get(AGGREGATION_NAME);
                    return valueCount.value();
                })
                .build();
    }

    public ElasticSearchDAO(String clusterName, String clusterAddress) {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
        Settings settings = Settings.builder()
                .put("cluster.name", clusterName)//集群名称
                .put("client.transport.sniff", "true")
                .build();

        if (client == null) {
            synchronized (ElasticSearchDAO.class) {
                if (client == null) {
                    try {
                        client = new PreBuiltTransportClient(settings).addTransportAddress(
                                new InetSocketTransportAddress(InetAddress.getByName(clusterAddress), ESPORT)
                        );
                    } catch (UnknownHostException e) {
                        log.error("ElasticSearchDAO init error! UnknowHostException={}", e.getMessage(), e);
                        client.close();
                    }
                }
            }
        }
        this.response = client.prepareSearch(DEFAULT_INDEX)
                .setTypes(DEFAULT_TYPE);
        this.resultType = CRITICAL_INFO;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> DataSourcePO<T> get() {
        long start = System.currentTimeMillis();
        if (aggregation != null) {
            SearchResponse searchResponse = response.setFrom(0).get();
            Map<String, Map<String, Object>> aggMap = aggTransformToMap(searchResponse);
            return (DataSourcePO<T>) statisticResultToJSON(aggMap);
        }
        SearchResponse searchResponse = response.setFrom(0)
                .setSize(MAX_DOC_NUM)
                .setTerminateAfter(MAX_DOC_NUM)
                .get();

        List<Map<String, Object>> queryMap = transformToMap(searchResponse);
        if (INDEX.equals(this.resultType)) {
            return (DataSourcePO<T>) transferIndexResultToJSON(queryMap);
        }
        if (EXTRA_INFO.equals(this.resultType)) {
            return (DataSourcePO<T>) transferExtraResultToJSON(queryMap);
        }
        return (DataSourcePO<T>) transferCriticalResultToJSON(queryMap);
    }

    @Override
    public <T> boolean insert(String tableName, DataSourcePO<T> content) {
        return false;
    }

    @Override
    public ElasticSearchDAO must(Map<String, Object> addCondition) {
        if (addCondition.isEmpty()) {
            return this;
        }
        if (this.queryCondition == null) {
            this.queryCondition = QueryBuilders.boolQuery();
        }
        fieldContentConversion(addCondition);
        for (Map.Entry<String, Object> entry : addCondition.entrySet()) {
            if (TIMELIMIT.equals(entry.getKey())) {
                List<String> timeRange = getUsableTimeRange(addCondition);
                String from = mills2Format(DATE_FORMAT, Long.parseLong(timeRange.get(0)) * 1000);
                String to = mills2Format(DATE_FORMAT, Long.parseLong(timeRange.get(1)) * 1000);
                this.queryCondition.filter(QueryBuilders.rangeQuery(TIME.getTerm())
                        .from(from)
                        .to(to)
                        .format(DATE_FORMAT)
                        .timeZone(TIME_ZONE));
                continue;
            }
            if (CriticalLogTermsEnum.toList().contains(entry.getKey())) {
                this.queryCondition.filter(QueryBuilders.termQuery(entry.getKey(), entry.getValue()));
                continue;
            }
            if (CriticalLogTermsEnum.toList().contains(entry.getKey().split(SEPARATOR)[0])) {
                this.queryCondition.filter(QueryBuilders.termQuery(entry.getKey().split(SEPARATOR)[0], entry.getValue()));
            }
        }
        response.setQuery(queryCondition);
        return this;
    }

    @Override
    public ElasticSearchDAO should(Map<String, Object> addCondition) {
        if (addCondition.isEmpty()) {
            return this;
        }
        if (this.queryCondition == null) {
            this.queryCondition = QueryBuilders.boolQuery();
        }
        BoolQueryBuilder shouldQuery = QueryBuilders.boolQuery();
        fieldContentConversion(addCondition);
        for (Map.Entry<String, Object> entry : addCondition.entrySet()) {
            if (ACTIONNAME.equals(entry.getKey())) {
                List<String> usableAction = getUsableActionList(addCondition);
                for (String item : usableAction) {
                    shouldQuery.should(QueryBuilders.termQuery(ACTION.getTerm(), item));
                }
                continue;
            }
            if (CriticalLogTermsEnum.toList().contains(entry.getKey())) {
                shouldQuery.should(QueryBuilders.termQuery(entry.getKey(), entry.getValue()));
                continue;
            }
            if (CriticalLogTermsEnum.toList().contains(entry.getKey().split(SEPARATOR)[0])) {
                shouldQuery.should(QueryBuilders.termQuery(entry.getKey().split(SEPARATOR)[0], entry.getValue()));
            }
        }
        this.queryCondition.filter(shouldQuery);
        response.setQuery(queryCondition);
        return this;
    }

    @Override
    public ElasticSearchDAO mustNot(Map<String, Object> addCondition) {
        if (addCondition.isEmpty()) {
            return this;
        }
        if (this.queryCondition == null) {
            this.queryCondition = QueryBuilders.boolQuery();
        }
        fieldContentConversion(addCondition);
        for (Map.Entry<String, Object> entry : addCondition.entrySet()) {
            if (CriticalLogTermsEnum.toList().contains(entry.getKey())) {
                this.queryCondition.mustNot(QueryBuilders.termQuery(entry.getKey(), entry.getValue()));
                continue;
            }
            if (CriticalLogTermsEnum.toList().contains(entry.getKey().split(SEPARATOR)[0])) {
                this.queryCondition.mustNot(QueryBuilders.termQuery(entry.getKey().split(SEPARATOR)[0], entry.getValue()));
            }
        }
        response.setQuery(queryCondition);
        return this;
    }

    @Override
    public ElasticSearchDAO max(String bucket, String metrics, DateHistogramInterval interval, boolean needHistogram) {
        GroupAndCount groupAndCount = (String met)
                -> AggregationBuilders.max(AGGREGATION_NAME).field(met);
        this.aggregation = groupAndCount.invoke(bucket, metrics, needHistogram, interval);
        this.response.addAggregation(this.aggregation);
        return this;
    }

    @Override
    public ElasticSearchDAO min(String bucket, String metrics, DateHistogramInterval interval, boolean needHistogram) {
        GroupAndCount groupAndCount = (String met)
                -> AggregationBuilders.min(AGGREGATION_NAME).field(met);
        this.aggregation = groupAndCount.invoke(bucket, metrics, needHistogram, interval);
        this.response.addAggregation(this.aggregation);
        return this;
    }

    @Override
    public ElasticSearchDAO sum(String bucket, String metrics, DateHistogramInterval interval, boolean needHistogram) {
        GroupAndCount groupAndCount = (String met)
                -> AggregationBuilders.sum(AGGREGATION_NAME).field(met);
        this.aggregation = groupAndCount.invoke(bucket, metrics, needHistogram, interval);
        this.response.addAggregation(this.aggregation);
        return this;
    }

    @Override
    public ElasticSearchDAO avg(String bucket, String metrics, DateHistogramInterval interval, boolean needHistogram) {
        GroupAndCount groupAndCount = (String met)
                -> AggregationBuilders.avg(AGGREGATION_NAME).field(met);
        this.aggregation = groupAndCount.invoke(bucket, metrics, needHistogram, interval);
        this.response.addAggregation(this.aggregation);
        return this;
    }

    @Override
    public ElasticSearchDAO cnt(String bucket, String metrics, DateHistogramInterval interval, boolean needHistogram) {
        GroupAndCount groupAndCount = (String met)
                -> AggregationBuilders.count(AGGREGATION_NAME).field(met);
        this.aggregation = groupAndCount.invoke(bucket, metrics, needHistogram, interval);
        this.response.addAggregation(this.aggregation);
        return this;
    }

    @Override
    public ElasticSearchDAO distinct_cnt(String bucket, String metrics, DateHistogramInterval interval, boolean needHistogram) {
        GroupAndCount groupAndCount = (String met)
                -> AggregationBuilders.cardinality(AGGREGATION_NAME).field(met);
        this.aggregation = groupAndCount.invoke(bucket, metrics, needHistogram, interval);
        this.response.addAggregation(this.aggregation);
        return this;
    }

    @Override
    public ElasticSearchDAO clear() {
        this.queryCondition = null;
        this.aggregation = null;
        this.response = client.prepareSearch(DEFAULT_INDEX)
                .setTypes(DEFAULT_TYPE);
        this.resultType = CRITICAL_INFO;
        return this;
    }

    interface GroupAndCount {
        AggregationBuilder metric(String met);

        default AggregationBuilder invoke(String bucket, String metrics, boolean needHistogram, DateHistogramInterval interval) {
            AggregationBuilder aggregation = metric(metrics);
            if (needHistogram) {
                aggregation = AggregationBuilders.dateHistogram(DATE_BUCKET_NAME)
                        .field(TIME.getTerm())
                        .dateHistogramInterval(interval)
                        .format(DATE_FORMAT)
                        .timeZone(DateTimeZone.forID(TIME_ZONE))
                        .minDocCount(0)
                        .subAggregation(aggregation);
            }
            if (StringUtils.isNotBlank(bucket)) {
                aggregation = AggregationBuilders
                        .terms(METRICS_BUCKET_NAME)
                        .field(bucket)
                        .size(MAX_AGG_NUM)
                        .order(Terms.Order.count(false))
                        .subAggregation(aggregation);
            }
            return aggregation;
        }
    }

    interface Value {
        Object get(Aggregations aggregations);
    }

    private static List<Map<String, Object>> transformToMap(SearchResponse originalData) {
        ArrayList<Map<String, Object>> searchResultList = new ArrayList<>();

        for (SearchHit hit : originalData.getHits().getHits()) {
            searchResultList.add(hit.getSourceAsMap());
        }

        return searchResultList;
    }

    private static Map<String, Map<String, Object>> aggTransformToMap(SearchResponse originalData) {

        Map<String, Map<String, Object>> result = new LinkedHashMap<>();
        Terms terms = originalData.getAggregations().get(METRICS_BUCKET_NAME);
        for (Terms.Bucket bucket : terms.getBuckets()) {
            String key = String.valueOf(bucket.getKey());
            Map<String, Object> subMap = new HashMap<>();
            InternalDateHistogram dateHistogram = bucket.getAggregations().get(DATE_BUCKET_NAME);
            for (InternalDateHistogram.Bucket subBucket : dateHistogram.getBuckets()) {
                Aggregations aggregations = subBucket.getAggregations();
                subMap.put(subBucket.getKeyAsString(), aggregationValue
                        .get(aggregations.get(AGGREGATION_NAME).getType())
                        .get(aggregations));
            }
            result.put(key, subMap);
        }
        return result;
    }

    /**
     * ES-特殊字段转换
     */
    private void fieldContentConversion(Map<String, Object> map) {
        String hbaseTableName = String.valueOf(map.get(QUERY_TABLE));
        if (!"null".equals(hbaseTableName) && DETAILS.equals(hbaseTableName)) {
            this.resultType = EXTRA_INFO;
        }
        if (map.get(QUERY_INDEX) != null && Boolean.parseBoolean(map.get(QUERY_INDEX).toString())) {
            this.resultType = INDEX;
            this.response = client.prepareSearch(MOBILE_INDEX)
                    .setTypes(DEFAULT_TYPE);
        }
        map.remove(QUERY_TABLE);
        map.remove(QUERY_INDEX);
    }

    private static DataSourcePO<List<CriticalLogTermsDTO>> transferCriticalResultToJSON(List<Map<String, Object>> result) {
        List<CriticalLogTermsDTO> ds = Lists.newArrayList();
        for (Map<String, Object> item : result) {
            if (item.isEmpty()) {
                continue;
            }
            CriticalLogTermsDTO criticalLogTermsDTO = new CriticalLogTermsDTO();
            String dateTime = String.valueOf(item.get("mt_datetime"));
            criticalLogTermsDTO.setTime("null".equals(dateTime) ? "0" : reformatDateStrDefault(dateTime, DATE_FORMAT));
            criticalLogTermsDTO.setUserId(String.valueOf(item.get("userid")));
            criticalLogTermsDTO.setUuid(String.valueOf(item.get("uuid")));
            criticalLogTermsDTO.setIp(String.valueOf(item.get("mt_clientip")));
            criticalLogTermsDTO.setAppName(String.valueOf(item.get("appName")));
            criticalLogTermsDTO.setUserName(String.valueOf(item.get("username")));
            criticalLogTermsDTO.setStatus(String.valueOf(item.get("status")));
            criticalLogTermsDTO.setErrMsg(String.valueOf(item.get("errmsg")));
            criticalLogTermsDTO.setSource(String.valueOf(item.get("source")));
            criticalLogTermsDTO.setMobile(String.valueOf(item.get("mobile")));
            criticalLogTermsDTO.setOldMobile(String.valueOf(item.get("oldMobile")));
            criticalLogTermsDTO.setAction(String.valueOf(item.get("action")));
            criticalLogTermsDTO.setPlatform(String.valueOf(item.get("platform")));
            criticalLogTermsDTO.setPartner(String.valueOf(item.get("partner")));
            criticalLogTermsDTO.setAppnm(String.valueOf(item.get("appnm")));
            ds.add(criticalLogTermsDTO);
        }
        ds.sort((o1, o2) -> {
            if (StringUtils.isBlank(o1.getTime()) && StringUtils.isBlank(o2.getTime())) {
                return 0;
            }
            if (!StringUtils.isBlank(o1.getTime()) && StringUtils.isBlank(o2.getTime())) {
                return -1;
            }
            if (StringUtils.isBlank(o1.getTime())) {
                return 1;
            }
            long time1 = stringToLong(o1.getTime(), DEFAULT_TIME_FORMAT);
            long time2 = stringToLong(o2.getTime(), DEFAULT_TIME_FORMAT);
            if (time1 < time2) {
                return 1;
            }
            if (time1 == time2) {
                return 0;
            }
            return -1;
        });
        DataSourcePO<List<CriticalLogTermsDTO>> resultModel = new DataSourcePO<>();
        resultModel.setSuccess(true);
        resultModel.setResult(ds);
        resultModel.setType(ONLY_QUERY);
        return resultModel;
    }

    private static DataSourcePO<List<Map<String, Object>>> transferIndexResultToJSON(List<Map<String, Object>> resultMap) {
        DataSourcePO<List<Map<String, Object>>> resultModel = new DataSourcePO<>();
        resultModel.setSuccess(true);
        resultModel.setResult(resultMap);
        return resultModel;
    }

    private static DataSourcePO<List<ExtraLogTermsDTO>> transferExtraResultToJSON(List<Map<String, Object>> resultMap) {
        List<ExtraLogTermsDTO> ds = Lists.newArrayList();
        for (Map<String, Object> item : resultMap) {
            if (item.isEmpty()) {
                continue;
            }
            ExtraLogTermsDTO extraLogTermsDTO = new ExtraLogTermsDTO();
            String dateTime = String.valueOf(item.get("mt_datetime"));
            extraLogTermsDTO.setTime(String.valueOf("null".equals(dateTime) ? "0" : reformatDateStrDefault(dateTime, DATE_FORMAT)));
            extraLogTermsDTO.setUserId(String.valueOf(item.get("userid")));
            extraLogTermsDTO.setUuid(String.valueOf(item.get("uuid")));
            extraLogTermsDTO.setIp(String.valueOf(item.get("mt_clientip")));
            extraLogTermsDTO.setData(String.valueOf(item.get("data")));
            extraLogTermsDTO.setSignuptype(String.valueOf(item.get("signuptype")));
            extraLogTermsDTO.setLogintype(String.valueOf(item.get("logintype")));
            extraLogTermsDTO.setUseragent(String.valueOf(item.get("useragent")));
            ds.add(extraLogTermsDTO);
        }
        DataSourcePO<List<ExtraLogTermsDTO>> resultModel = new DataSourcePO<>();
        resultModel.setSuccess(true);
        resultModel.setResult(ds);
        resultModel.setType(QUERY_DETAIL_OR_STATISTIC);
        return resultModel;
    }

    private static DataSourcePO<List<List<String>>> statisticResultToJSON(Map<String, Map<String, Object>> result) {
        List<List<String>> showResult = new ArrayList<>();
        List<String> title = new ArrayList<>();
        List<Map<String, Object>> context = new ArrayList<>();
        title.add("Time");
        for (Map.Entry<String, Map<String, Object>> entry : result.entrySet()) {
            title.add(entry.getKey());
            context.add(entry.getValue());
        }

        for (int cnt = 0; cnt < context.size(); cnt++) {
            Map<String, Object> item = context.get(cnt);
            List<String> showItem;
            if (cnt == 0) {
                for (Map.Entry<String, Object> entry : item.entrySet()) {
                    showItem = new ArrayList<>();
                    showItem.add(entry.getKey());
                    showItem.add(String.valueOf(entry.getValue()));
                    showResult.add(showItem);
                }
            } else {
                for (int cnt2 = 0; cnt2 < showResult.size(); cnt2++) {
                    showItem = showResult.get(cnt2);
                    String time = String.valueOf(showItem.get(0));
                    String value = String.valueOf(item.get(time));
                    if ("null".equals(value)) {
                        value = "0";
                    }
                    showItem.add(value);
                    showResult.set(cnt2, showItem);
                }
            }
        }
        showResult.sort((o1, o2) -> {
            if (StringUtils.isBlank(o1.get(0)) && StringUtils.isBlank(o2.get(0))) {
                return 0;
            }
            if (!StringUtils.isBlank(o1.get(0)) && StringUtils.isBlank(o2.get(0))) {
                return -1;
            }
            if (StringUtils.isBlank(o1.get(0))) {
                return 1;
            }
            long time1 = stringToLong(o1.get(0), DATE_FORMAT);
            long time2 = stringToLong(o2.get(0), DATE_FORMAT);
            if (time1 < time2) {
                return 1;
            }
            if (time1 == time2) {
                return 0;
            }
            return -1;
        });

        DataSourcePO<List<List<String>>> resultModel = new DataSourcePO<>();
        resultModel.setSuccess(true);
        resultModel.setResult(showResult);
        resultModel.setTitle(title);
        resultModel.setType(QUERY_DETAIL_OR_STATISTIC);
        return resultModel;
    }

    /**
     * 获取可用的时间查询范围，结果为今天与指定时间范围的交集。默认时间为今日凌晨到当前时刻，格式为十位数字型字符.
     */
    private List<String> getUsableTimeRange(Map<String, Object> condition) {
        List<String> timeRange = new ArrayList<>();
        String timeLimit = String.valueOf(condition.get(TIMELIMIT));
        String[] timeLimits = timeLimit.split(TIMELIMIT_SEPARATOR);
        timeRange.add(timeLimits[0]);
        timeRange.add(timeLimits[1]);
        return timeRange;
    }

    /**
     * 返回可用的操作类型，默认为空
     */
    private List<String> getUsableActionList(Map<String, Object> addCondition) {
        Object actionNames = addCondition.get(ACTIONNAME);
        List<String> actionList = new ArrayList<>();
        String[] actions = actionNames.toString().split(ACTION_SEPARATOR);
        for (String action : actions) {
            actionList.add(ActionEnum.getNameToActionMap().get(action));
        }
        return actionList;
    }
}
