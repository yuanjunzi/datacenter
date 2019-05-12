package com.git.service.yuanjunzi.datacenter.domain.repository;

import com.git.service.yuanjunzi.datacenter.constants.ActionEnum;
import com.git.service.yuanjunzi.datacenter.domain.dto.DataCenterInputInfoDTO;
import com.git.service.yuanjunzi.datacenter.domain.dto.ErrorDTO;
import com.git.service.yuanjunzi.datacenter.infrastructure.dao.DataSource;
import com.git.service.yuanjunzi.datacenter.infrastructure.dao.ElasticSearchDAO;
import com.git.service.yuanjunzi.datacenter.infrastructure.utils.TimeUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;

import static com.git.service.yuanjunzi.datacenter.constants.CriticalLogTermsEnum.ACTION;
import static com.git.service.yuanjunzi.datacenter.constants.CriticalLogTermsEnum.TIME;
import static com.git.service.yuanjunzi.datacenter.domain.converter.DataCenterInputInfoConverter.*;
import static com.git.service.yuanjunzi.datacenter.infrastructure.utils.TimeUtils.*;

/**
 * Created by yuanjunzi on 2019/4/25.
 * 数据平台参数解析策略集合
 */
public abstract class DataCenterParamResolutionStrategy {

    public static String TIMELIMIT_SEPARATOR = ",";

    public static String ACTION_SEPARATOR = ",";

    public static final String QUERY_LAST_ONE_HOUR = "queryLastOneHour";

    public static final String QUERY_LAST_ONE_HOUR_LAST_WEEK = "queryLastOneHourLastWeek";

    public static final String COMPLETING_DATA_TODAY = "completingDataToday";

    void prepareElasticSearchParam(DataCenterInputInfoDTO inputInfo) {
        elasticsearchFieldContentConversion(inputInfo.getMust());
        elasticsearchFieldContentConversion(inputInfo.getMustNot());
        elasticsearchFieldContentConversion(inputInfo.getShould());

        getESUsableTimeRange(inputInfo.getMust());
    }

    void prepareHBaseParam(DataCenterInputInfoDTO inputInfo) {
        getUsableActionList(inputInfo.getMust());

        getUsableTimeLimit(inputInfo.getMust());
    }

    /**
     * ES-特殊字段转换
     */
    private void elasticsearchFieldContentConversion(Map<String, Object> map) {
        String time = String.valueOf(map.get(TIME.getTerm()));
        if (!"null".equals(time)) {
            map.put(TIME.getTerm(), TimeUtils.reformatDateStr(time, TimeUtils.DEFAULT_TIME_FORMAT, ElasticSearchDAO.DATE_FORMAT));
        }
        String action = String.valueOf(map.get(ACTION.getTerm()));
        if (!"null".equals(action) && StringUtils.isNotBlank(ActionEnum.getNameToActionMap().get(action))) {
            map.put(ACTION.getTerm(), ActionEnum.getNameToActionMap().get(action));
        }
        map.remove(QUERYDIMENSION);
    }

    /**
     * 获取ES可用的时间查询范围。默认时间为80天前到当前时刻，格式为(startLong, endLong) 秒级
     *
     * @Param condition 中，时间范围为毫秒级.
     */
    private void getESUsableTimeRange(Map<String, Object> condition) {
        String startTimeStrDefault = getTimeBefore(Calendar.DATE, -80);
        String endTimeStrDefault = getCurrentMoment();
        Long[] startTimes = {0L, 0L, 0L, 0L};
        Long[] endTimes = {3449987187L, 3449987187L, 3449987187L, 3449987187L};

        startTimes[0] = stringToLong(startTimeStrDefault, TimeUtils.DEFAULT_TIME_FORMAT);
        endTimes[0] = stringToLong(endTimeStrDefault, TimeUtils.DEFAULT_TIME_FORMAT);
        if (condition.get(QUERY_LAST_ONE_HOUR) != null) {
            String startStr = getTimeBefore(Calendar.HOUR_OF_DAY, -1);
            String endStr = getTimeBefore(Calendar.HOUR_OF_DAY, 0);
            startTimes[1] = stringToLong(startStr, TimeUtils.DEFAULT_TIME_FORMAT);
            endTimes[1] = stringToLong(endStr, TimeUtils.DEFAULT_TIME_FORMAT);
            condition.remove(QUERY_LAST_ONE_HOUR);
        }
        if (condition.get(COMPLETING_DATA_TODAY) != null) {
            startTimes[2] = stringToLong(getStartTime(), TimeUtils.DEFAULT_TIME_FORMAT);
            endTimes[2] = stringToLong(getCurrentMoment(), TimeUtils.DEFAULT_TIME_FORMAT);
            condition.remove(COMPLETING_DATA_TODAY);
        }
        if (condition.get(TIMELIMIT) != null) {
            String timeLimit = String.valueOf(condition.get(TIMELIMIT));
            String[] timeLimits = timeLimit.split(TIMELIMIT_SEPARATOR);
            startTimes[3] = Long.parseLong(timeLimits[0]) / 1000;
            endTimes[3] = Long.parseLong(timeLimits[1]) / 1000;
        }

        long startTime = (long) Collections.max(Arrays.asList(startTimes));
        long endTime = (long) Collections.min(Arrays.asList(endTimes));
        condition.put(TIMELIMIT, startTime + TIMELIMIT_SEPARATOR + endTime);
    }

    /**
     * 返回HBase可用的操作类型，默认为全选
     */
    private void getUsableActionList(Map<String, Object> addCondition) {
        if (addCondition.get(ACTIONNAME) == null) {
            List<String> actionCodeList = ActionEnum.getActionNameList();
            addCondition.put(ACTIONNAME, StringUtils.join(actionCodeList, ACTION_SEPARATOR));
        }
    }

    /**
     * 返回HBase可用的时间条件，格式为(startLong, endLong)，默认为2012-9-12至今
     */
    private void getUsableTimeLimit(Map<String, Object> addCondition) {
        Object time = addCondition.get(TIMELIMIT);
        long startTime = 1347379200;
        long endTime = stringToLong(getStartTime(), TimeUtils.DEFAULT_TIME_FORMAT);
        if (time != null) {
            String timeLimit = time.toString();
            String[] timeRange = timeLimit.split(TIMELIMIT_SEPARATOR);
            if (timeRange.length == 2) {
                long time1 = Long.parseLong(timeRange[0]) / 1000;
                long time2 = Long.parseLong(timeRange[1]) / 1000;
                startTime = startTime > time1 ? startTime : time1;
                endTime = endTime < time2 ? endTime : time2;
            }
        }
        addCondition.put(TIMELIMIT, startTime + TIMELIMIT_SEPARATOR + endTime);
    }

    abstract public ErrorDTO resolution(DataSource dataSource, DataCenterInputInfoDTO inputInfo);
}
