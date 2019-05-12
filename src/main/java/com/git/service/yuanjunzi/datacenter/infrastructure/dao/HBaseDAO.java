package com.git.service.yuanjunzi.datacenter.infrastructure.dao;

import com.git.service.yuanjunzi.datacenter.constants.ActionEnum;
import com.git.service.yuanjunzi.datacenter.domain.dto.CriticalLogTermsDTO;
import com.git.service.yuanjunzi.datacenter.domain.dto.ExtraLogTermsDTO;
import com.git.service.yuanjunzi.datacenter.domain.dto.PassportDTO;
import com.git.service.yuanjunzi.datacenter.domain.factory.AbstractUsableDataFactory;
import com.git.service.yuanjunzi.datacenter.infrastructure.dao.po.DataSourcePO;
import com.git.service.yuanjunzi.datacenter.infrastructure.utils.TimeUtils;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.MultiRowRangeFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;

import java.io.IOException;
import java.util.*;

import static com.git.service.yuanjunzi.datacenter.application.service.DataProcessFacade.ONLY_QUERY;
import static com.git.service.yuanjunzi.datacenter.application.service.DataProcessFacade.QUERY_DETAIL_OR_STATISTIC;
import static com.git.service.yuanjunzi.datacenter.constants.CriticalLogTermsEnum.USERID;
import static com.git.service.yuanjunzi.datacenter.domain.converter.DataCenterInputInfoConverter.ACTIONNAME;
import static com.git.service.yuanjunzi.datacenter.domain.converter.DataCenterInputInfoConverter.TIMELIMIT;
import static com.git.service.yuanjunzi.datacenter.domain.repository.DataCenterParamResolutionStrategy.ACTION_SEPARATOR;
import static com.git.service.yuanjunzi.datacenter.domain.repository.DataCenterParamResolutionStrategy.TIMELIMIT_SEPARATOR;
import static com.git.service.yuanjunzi.datacenter.infrastructure.utils.TimeUtils.stringToLong;

/**
 * Created by yuanjunzi on 2018/12/18.
 * 用于查询存储在HBase中的数据
 * 不应直接实例化。建议通过 {@link AbstractUsableDataFactory} 完成实例化
 */

@Slf4j
public class HBaseDAO implements DataSource {

    public static final String BRIEF = "bp_user_briefpassport";

    public static final String DETAILS = "bp_user_detailedpassport";

    public static final String QUERY_TABLE = "tableName";

    /**
     * 精确查询标识。默认为模糊扫描
     */
    public static final String EXACT_QUERY = "exactQuery";

    private static final String BRIEF_FAMILY = "brief";

    private static final String DETAILS_FAMILY = "details";

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static final Map<String, InsertStatement> insertStatementMap = ImmutableMap.<String, InsertStatement>builder()
            .put(BRIEF, (Put put, PassportDTO passportDTO, long ts) -> {
                put.addColumn(Bytes.toBytes(BRIEF_FAMILY), Bytes.toBytes("datetime"), ts, Bytes.toBytes(passportDTO.get_mt_datetime()));
                put.addColumn(Bytes.toBytes(BRIEF_FAMILY), Bytes.toBytes("action"), ts, Bytes.toBytes(passportDTO.getAction()));
                put.addColumn(Bytes.toBytes(BRIEF_FAMILY), Bytes.toBytes("appnm"), ts, Bytes.toBytes(passportDTO.getAppnm()));
                put.addColumn(Bytes.toBytes(BRIEF_FAMILY), Bytes.toBytes("err"), ts, Bytes.toBytes(passportDTO.getErr()));
                put.addColumn(Bytes.toBytes(BRIEF_FAMILY), Bytes.toBytes("mobile"), ts, Bytes.toBytes(passportDTO.getStrMobile()));
                put.addColumn(Bytes.toBytes(BRIEF_FAMILY), Bytes.toBytes("oldmobile"), ts, Bytes.toBytes(passportDTO.getOldMobile()));
                put.addColumn(Bytes.toBytes(BRIEF_FAMILY), Bytes.toBytes("partner"), ts, Bytes.toBytes(passportDTO.getPartner()));
                put.addColumn(Bytes.toBytes(BRIEF_FAMILY), Bytes.toBytes("platform"), ts, Bytes.toBytes(passportDTO.getPlatform()));
                put.addColumn(Bytes.toBytes(BRIEF_FAMILY), Bytes.toBytes("source"), ts, Bytes.toBytes(passportDTO.getSource()));
                put.addColumn(Bytes.toBytes(BRIEF_FAMILY), Bytes.toBytes("userid"), ts, Bytes.toBytes(passportDTO.getUserid()));
                put.addColumn(Bytes.toBytes(BRIEF_FAMILY), Bytes.toBytes("username"), ts, Bytes.toBytes(passportDTO.getUsername()));
            })
            .put(DETAILS, (Put put, PassportDTO passportDTO, long ts) -> {
                put.addColumn(Bytes.toBytes(DETAILS_FAMILY), Bytes.toBytes("datetime"), ts, Bytes.toBytes(passportDTO.get_mt_datetime()));
                put.addColumn(Bytes.toBytes(DETAILS_FAMILY), Bytes.toBytes("clientip"), ts, Bytes.toBytes(passportDTO.getIpStr()));
                put.addColumn(Bytes.toBytes(DETAILS_FAMILY), Bytes.toBytes("extrainfo"), ts, Bytes.toBytes(passportDTO.getData()));
                put.addColumn(Bytes.toBytes(DETAILS_FAMILY), Bytes.toBytes("logintype"), ts, Bytes.toBytes(passportDTO.getLogintype()));
                put.addColumn(Bytes.toBytes(DETAILS_FAMILY), Bytes.toBytes("signuptype"), ts, Bytes.toBytes(passportDTO.getSignuptype()));
                put.addColumn(Bytes.toBytes(DETAILS_FAMILY), Bytes.toBytes("useragent"), ts, Bytes.toBytes(passportDTO.getUseragent()));
                put.addColumn(Bytes.toBytes(DETAILS_FAMILY), Bytes.toBytes("userid"), ts, Bytes.toBytes(passportDTO.getUserid()));
                put.addColumn(Bytes.toBytes(DETAILS_FAMILY), Bytes.toBytes("uuid"), ts, Bytes.toBytes(passportDTO.getUuid()));
            })
            .build();

    private Scan scan;

    private List<Get> gets;

    private String tableName = BRIEF;

    private List<Map<String, Object>> resultList;

    @Override
    @SuppressWarnings("unchecked")
    public <T> DataSourcePO<T> get() {
        HBaseClientImpl client = HBaseClientImpl.getInstance();
        long start = System.currentTimeMillis();

        if ((this.scan == null || this.scan.getFilter() == null) && (this.gets == null || this.gets.isEmpty())) {
            resultList = new ArrayList<>();
        } else if (this.gets != null && !this.gets.isEmpty()) {
            Result[] result;
            try {
                result = client.get(this.tableName, this.gets);
                resultList = transResultToMap(result);
            } catch (Exception e) {
                log.error("HBaseDAO get info error! exception={}", e.getMessage(), e);
                resultList = new ArrayList<>();
            }
        } else {
            try {
                client.scan(this.tableName, this.scan, (ResultScanner result) -> this.resultList = transResultToMap(result));
            } catch (Exception e) {
                log.error("HBaseDAO scan info error! exception={}", e.getMessage(), e);
                resultList = new ArrayList<>();
            }
        }

        if (BRIEF.equals(this.tableName)) {
            return (DataSourcePO<T>) transCriticalResultMapToJSON(resultList);
        }
        return (DataSourcePO<T>) transExtraResultMapToJSON(resultList);
    }

    @Override
    public <T> boolean insert(String tableName, DataSourcePO<T> dataSourcePO) {
        T content = dataSourcePO.getResult();
        if (content instanceof PassportDTO) {
            PassportDTO passportDTO = (PassportDTO) content;
            String userId = passportDTO.getUserid();
            if ("".equals(userId)) {
                return false;
            }
            Long unixTime = stringToLong(passportDTO.get_mt_datetime(), DATE_FORMAT);
            String rowKey = DigestUtils.md5Hex(String.valueOf(userId)) + "_" + DigestUtils.md5Hex(passportDTO.getAction()) + "_" + unixTime;
            Put put = new Put(Bytes.toBytes(rowKey));
            InsertStatement insertStatement = insertStatementMap.get(tableName);
            if (insertStatement == null) {
                return false;
            }
            insertStatement.invoke(put, passportDTO, TimeUtils.currentTimeMillis());
            HBaseClientImpl client = HBaseClientImpl.getInstance();
            try {
                client.put(tableName, put);
            } catch (IOException e) {
                log.error("insert hbase fail, exception={}", e.getMessage(), e);
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public HBaseDAO must(Map<String, Object> addCondition) {
        if (addCondition.isEmpty()) {
            return this;
        }
        String tableName = String.valueOf(addCondition.get(QUERY_TABLE));
        if (DETAILS.equals(tableName)) {
            this.tableName = DETAILS;
            addCondition.remove(QUERY_TABLE);
        }
        if (addCondition.get(EXACT_QUERY) != null && Boolean.parseBoolean(addCondition.get(EXACT_QUERY).toString())) {
            this.gets = getGets(addCondition);
            return this;
        }
        Filter filter = getFilter(addCondition);
        if (filter == null) {
            return this;
        }
        if (this.scan == null) {
            this.scan = new Scan();
        }
        this.scan.setFilter(filter);
        return this;
    }

    @Override
    public HBaseDAO should(Map<String, Object> addCondition) {
        return this;
    }

    @Override
    public HBaseDAO mustNot(Map<String, Object> addCondition) {
        return this;
    }

    @Override
    public HBaseDAO max(String bucket, String metrics, DateHistogramInterval interval
            , boolean needHistogram) {
        return this;
    }

    @Override
    public HBaseDAO min(String bucket, String metrics, DateHistogramInterval interval, boolean needHistogram) {
        return this;
    }

    @Override
    public HBaseDAO sum(String bucket, String metrics, DateHistogramInterval interval, boolean needHistogram) {
        return this;
    }

    @Override
    public HBaseDAO avg(String bucket, String metrics, DateHistogramInterval interval, boolean needHistogram) {
        return this;
    }

    @Override
    public HBaseDAO cnt(String bucket, String metrics, DateHistogramInterval interval, boolean needHistogram) {
        return this;
    }

    @Override
    public HBaseDAO distinct_cnt(String bucket, String metrics, DateHistogramInterval interval, boolean needHistogram) {
        return this;
    }

    @Override
    public HBaseDAO clear() {
        this.scan = null;
        this.gets = null;
        this.tableName = BRIEF;
        return this;
    }

    private static List<Map<String, Object>> transResultToMap(ResultScanner result) {
        List<Map<String, Object>> showResult = new ArrayList<>();
        for (Result r : result) {
            Map<String, Object> map = new HashMap<>();
            for (Cell ce : r.rawCells()) {
                map.put(Bytes.toString(ce.getQualifierArray(), ce.getQualifierOffset(), ce.getQualifierLength())
                        , Bytes.toString(ce.getValueArray(), ce.getValueOffset(), ce.getValueLength()));
            }
            showResult.add(map);
        }
        result.close();
        sortQueryResult(showResult);
        return showResult;
    }

    private static List<Map<String, Object>> transResultToMap(Result[] result) {
        List<Map<String, Object>> showResult = new ArrayList<>();
        for (Result r : result) {
            Map<String, Object> map = new HashMap<>();
            for (Cell ce : r.rawCells()) {
                map.put(Bytes.toString(ce.getQualifierArray(), ce.getQualifierOffset(), ce.getQualifierLength())
                        , Bytes.toString(ce.getValueArray(), ce.getValueOffset(), ce.getValueLength()));
            }
            showResult.add(map);
        }
        sortQueryResult(showResult);
        return showResult;
    }

    private static void sortQueryResult(List<Map<String, Object>> showResult) {
        showResult.sort((o1, o2) -> {
            Object dateTime1 = o1.get("datetime");
            Object dateTime2 = o2.get("datetime");
            if (dateTime1 == null && dateTime2 == null) {
                return 0;
            }
            if (dateTime1 != null && dateTime2 == null) {
                return -1;
            }
            if (dateTime1 == null) {
                return 1;
            }
            long time1 = stringToLong(dateTime1.toString(), DATE_FORMAT);
            long time2 = stringToLong(dateTime2.toString(), DATE_FORMAT);
            if (time1 < time2) {
                return 1;
            }
            if (time1 == time2) {
                return 0;
            }
            return -1;
        });
    }

    private static DataSourcePO<List<CriticalLogTermsDTO>> transCriticalResultMapToJSON(List<Map<String, Object>> resultMap) {
        List<CriticalLogTermsDTO> ds = Lists.newArrayList();
        for (Map<String, Object> item : resultMap) {
            if (item.isEmpty()) {
                continue;
            }
            CriticalLogTermsDTO criticalLogTermsDTO = new CriticalLogTermsDTO();
            String dateTime = String.valueOf(item.get("datetime"));
            criticalLogTermsDTO.setTime("null".equals(dateTime) ? "0" : TimeUtils.reformatDateStrDefault(dateTime, DATE_FORMAT));
            criticalLogTermsDTO.setUserId(String.valueOf(item.get("userid")));
            criticalLogTermsDTO.setSource(String.valueOf(item.get("source")));
            criticalLogTermsDTO.setMobile(String.valueOf(item.get("mobile")));
            criticalLogTermsDTO.setOldMobile(String.valueOf(item.get("oldmobile")));
            criticalLogTermsDTO.setUserName(String.valueOf(item.get("username")));
            criticalLogTermsDTO.setAction(String.valueOf(item.get("action")));
            criticalLogTermsDTO.setPlatform(String.valueOf(item.get("platform")));
            criticalLogTermsDTO.setPartner(String.valueOf(item.get("partner")));
            criticalLogTermsDTO.setAppnm(String.valueOf(item.get("appnm")));
            String err = String.valueOf(item.get("err"));
            criticalLogTermsDTO.setErrMsg(String.valueOf(err));
            criticalLogTermsDTO.setStatus(String.valueOf(("null".equals(err) || StringUtils.isBlank(err)) ? "success" : "fail"));
            ds.add(criticalLogTermsDTO);
        }
        DataSourcePO<List<CriticalLogTermsDTO>> resultModel = new DataSourcePO<>();
        resultModel.setSuccess(true);
        resultModel.setResult(ds);
        resultModel.setType(ONLY_QUERY);
        return resultModel;
    }

    private static DataSourcePO<List<ExtraLogTermsDTO>> transExtraResultMapToJSON(List<Map<String, Object>> resultMap) {
        List<ExtraLogTermsDTO> ds = Lists.newArrayList();
        for (Map<String, Object> item : resultMap) {
            if (item.isEmpty()) {
                continue;
            }
            ExtraLogTermsDTO extraLogTermsDTO = new ExtraLogTermsDTO();
            String dateTime = String.valueOf(item.get("datetime"));
            extraLogTermsDTO.setTime(String.valueOf("null".equals(dateTime) ? "0" : TimeUtils.reformatDateStrDefault(dateTime, DATE_FORMAT)));
            extraLogTermsDTO.setUserId(String.valueOf(item.get("userid")));
            extraLogTermsDTO.setUuid(String.valueOf(item.get("uuid")));
            extraLogTermsDTO.setIp(String.valueOf(item.get("clientip")));
            extraLogTermsDTO.setData(String.valueOf(item.get("extrainfo")));
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

    private Filter getFilter(Map<String, Object> addCondition) {
        String userId = String.valueOf(addCondition.get(USERID.getTerm()));
        List<String> actionList = getUsableActionList(addCondition);
        String timeLimit = getUsableTimeLimit(addCondition);
        List<MultiRowRangeFilter.RowRange> rowRanges = assembleRowKey(userId, actionList, timeLimit);
        try {
            return new MultiRowRangeFilter(rowRanges);
        } catch (Exception e) {
            log.error("getFilter, new MultiRowRangeFilter error!", e);
        }
        return null;
    }

    private List<MultiRowRangeFilter.RowRange> assembleRowKey(String userId, List<String> actionList, String timeLimit) {
        String[] timeRange = timeLimit.split(TIMELIMIT_SEPARATOR);
        List<MultiRowRangeFilter.RowRange> rowRanges = new LinkedList<>();
        for (String actionItem : actionList) {
            String start = DigestUtils.md5Hex(userId) + "_" + DigestUtils.md5Hex(actionItem) + "_" + timeRange[0];
            String end = DigestUtils.md5Hex(userId) + "_" + DigestUtils.md5Hex(actionItem) + "_" + timeRange[1];
            MultiRowRangeFilter.RowRange rowRange = new MultiRowRangeFilter.RowRange(start, true, end, true);
            rowRanges.add(rowRange);

        }
        return rowRanges;
    }

    /**
     * 返回可用的操作类型，默认为全选
     */
    private List<String> getUsableActionList(Map<String, Object> addCondition) {
        Object actionNames = addCondition.get(ACTIONNAME);
        if (actionNames == null) {
            return ActionEnum.getActionCodeList();
        }
        List<String> actionList = new ArrayList<>();
        String[] actions = actionNames.toString().split(ACTION_SEPARATOR);
        for (String action : actions) {
            actionList.add(ActionEnum.getNameToActionMap().get(action));
        }
        return actionList;
    }

    /**
     * 返回可用的时间条件，格式为(startLong, endLong)，默认为2012-9-12至今
     */
    private String getUsableTimeLimit(Map<String, Object> addCondition) {
        return addCondition.get(TIMELIMIT).toString();
    }

    private List<Get> getGets(Map<String, Object> queryCondition) {
        queryCondition.remove(EXACT_QUERY);
        Set<String> set = duplicateRemoval(queryCondition);
        List<Get> getList = new ArrayList<>();
        for (String rowKey : set) {
            Get get = new Get(Bytes.toBytes(rowKey));
            getList.add(get);
        }
        return getList;
    }

    /**
     * rowkey去重
     */
    private Set<String> duplicateRemoval(Map<String, Object> queryCondition) {
        Set<String> set = new HashSet<>();
        String timeLimit = getUsableTimeLimit(queryCondition);
        List<String> actionList = getUsableActionList(queryCondition);
        Set<String> md5Actions = new HashSet<>();
        for (String item : actionList) {
            md5Actions.add(DigestUtils.md5Hex(item));
        }

        for (Object rowKey : queryCondition.values()) {
            String usableRowKey = filterOut(rowKey, md5Actions, timeLimit);
            if (usableRowKey != null) {
                set.add(usableRowKey);
            }
        }
        return set;
    }

    /**
     * 根据action和时间限制对rowkey做筛选。通过筛选转换为string返回，不通过的返回null
     */
    private String filterOut(Object rowKey, Set<String> md5Actions, String timeLimit) {
        if (rowKey == null) {
            return null;
        }
        String usableRowKey = rowKey.toString();
        String[] timeRange = timeLimit.split(TIMELIMIT_SEPARATOR);
        long startTime = Long.parseLong(timeRange[0]);
        long endTime = Long.parseLong(timeRange[1]);
        String[] rowKeyItems = usableRowKey.split("_");
        if (rowKeyItems.length != 3) {
            return null;
        }
        long time = Long.parseLong(rowKeyItems[2]);
        if (md5Actions.contains(rowKeyItems[1]) && time <= endTime && time >= startTime) {
            return usableRowKey;
        }
        return null;
    }

    interface InsertStatement {
        void invoke(Put put, PassportDTO passportDTO, long ts);
    }
}
