package com.git.service.yuanjunzi.datacenter.domain;

import cn.hutool.core.thread.NamedThreadFactory;
import com.git.service.yuanjunzi.datacenter.constants.ErrorMsgEnum;
import com.git.service.yuanjunzi.datacenter.constants.QueryDimensionEnum;
import com.git.service.yuanjunzi.datacenter.domain.dto.DataCenterInputInfoDTO;
import com.git.service.yuanjunzi.datacenter.domain.dto.ErrorDTO;
import com.git.service.yuanjunzi.datacenter.domain.factory.AbstractUsableDataFactory;
import com.git.service.yuanjunzi.datacenter.domain.repository.*;
import com.git.service.yuanjunzi.datacenter.infrastructure.dao.DataSource;
import com.git.service.yuanjunzi.datacenter.infrastructure.dao.po.DataSourcePO;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static com.git.service.yuanjunzi.datacenter.domain.converter.DataCenterInputInfoConverter.QUERYDIMENSION;
import static com.git.service.yuanjunzi.datacenter.infrastructure.MccManager.FULL_LOG_QUERY_TIMEOUT;
import static com.git.service.yuanjunzi.datacenter.infrastructure.dao.ElasticSearchDAO.INDEX_FOREIGNKEY;
import static com.git.service.yuanjunzi.datacenter.infrastructure.dao.HBaseDAO.*;
import static com.git.service.yuanjunzi.datacenter.infrastructure.dao.po.DataSourcePO.doIfError;


/**
 * Created by yuanjunzi on 2018/11/1.
 * 将数据接入层返回的数据做必要的处理和拼装，返回完整的数据.
 * 不应直接实例化。建议通过 {@link AbstractUsableDataFactory} 完成实例化
 */
@Slf4j
public class AssemblingDataRepository implements UsableDataRepository {

    private static final ThreadPoolExecutor FULL_QUERY_EXECUTOR = new ThreadPoolExecutor(
            8,
            32,
            30,
            TimeUnit.MINUTES,
            new ArrayBlockingQueue<>(16),
            new NamedThreadFactory("fullQueryThread", true),
            new ThreadPoolExecutor.AbortPolicy()
    );

    private static final DataCenterParamResolutionStrategy completionStrategy = new CompletionDataParamResolutionStrategy();

    private static final DataCenterParamResolutionStrategy queryIndexStrategy = new QueryIndexParamResolutionStrategy();

    private static final DataCenterParamResolutionStrategy commonQueryStrategy = new CommonQueryParamResolutionStrategy();

    private static final DataCenterParamResolutionStrategy commonStatisticStrategy = new CommonStatisticParamResolutionStrategy();

    private static final DataCenterParamResolutionStrategy queryAddtionalDataStrategy = new QueryAdditionalDataParamResolutionStrategy();

    private DataSource dataSource;

    private DataSource completionDataSource;

    private DataSource indexDataSource;

    public AssemblingDataRepository(DataSource originData) {
        this.dataSource = originData;
    }

    public AssemblingDataRepository(DataSource originData, DataSource completionData, DataSource indexDataSource) {
        this.dataSource = originData;
        this.completionDataSource = completionData;
        this.indexDataSource = indexDataSource;
    }

    /**
     * 获取全量数据
     */
    @Override
    public <T> DataSourcePO<List<T>> get() {
        if (dataSource == null) {
            return doIfError(ErrorMsgEnum.DEFAULT.getMessage());
        }
        return this.dataSource.get();
    }

    @Override
    public <T> void put(String tableName, T content) {
        boolean putSuccess;
        try {
            DataSourcePO<T> dataSourcePO = new DataSourcePO<>();
            dataSourcePO.setResult(content);
            putSuccess = dataSource.insert(tableName, dataSourcePO);
        } catch (Exception e) {
            log.error("put data error, exception = {}", e.getMessage(), e);
            return;
        }
        if (!putSuccess) {
            log.error("put data error");
        }
    }

    @Override
    public <T> DataSourcePO<List<T>> getWithCondition(DataCenterInputInfoDTO inputInfo) {
        if (dataSource == null) {
            return doIfError(ErrorMsgEnum.DEFAULT.getMessage());
        }
        DataSourcePO<List<T>> result;
        DataSourcePO<List<T>> completionResult;
        try {
            DataCenterInputInfoDTO inputInfo2 = inputInfo.copy();
            Future<DataSourcePO<List<T>>> futureResult = FULL_QUERY_EXECUTOR.submit(() -> queryResult(inputInfo));
            Future<DataSourcePO<List<T>>> futureCompletionResult = FULL_QUERY_EXECUTOR.submit(() -> completeQueryResult(inputInfo2));
            result = futureResult.get(FULL_LOG_QUERY_TIMEOUT, TimeUnit.SECONDS);
            completionResult = futureCompletionResult.get(FULL_LOG_QUERY_TIMEOUT, TimeUnit.SECONDS);
            buildUpFinalResult(result, completionResult);
            return result;
        } catch (TimeoutException e) {
            log.error("asyn query error! error={}", e.getMessage(), e);
            return doIfError("请求超时，请重试");
        } catch (Exception e) {
            log.error("get query result exception={}", e.getMessage(), e);
        }
        return doIfError(ErrorMsgEnum.SERVER_BUSY.getMessage());
    }

    @Override
    public DataSourcePO<List<List<String>>> countWithCondition(DataCenterInputInfoDTO inputInfo) {
        if (dataSource == null) {
            return doIfError(ErrorMsgEnum.DEFAULT.getMessage());
        }
        if (commonStatisticStrategy.resolution(dataSource, inputInfo) != null) {
            return doIfError(ErrorDTO.INNER_DATASOURCE_ERR.getMsg());
        }
        try {
            return dataSource.get();
        } catch (Exception e) {
            log.error("countWithCondition exception", e);
        }
        return doIfError(ErrorMsgEnum.SERVER_BUSY.getMessage());
    }

    private <T> DataSourcePO<List<T>> completeQueryResult(DataCenterInputInfoDTO inputInfo) {
        if (completionDataSource == null) {
            return null;
        }
        if (completionStrategy.resolution(completionDataSource, inputInfo) != null) {
            return null;
        }
        return this.completionDataSource.get();
    }

    private <T> DataSourcePO<List<T>> queryResult(DataCenterInputInfoDTO inputInfo) {
        if (isQueryAddtionalData(inputInfo)) {
            if (queryAddtionalDataStrategy.resolution(dataSource, inputInfo) != null) {
                return doIfError(ErrorDTO.INNER_DATASOURCE_ERR.getMsg());
            }
            return dataSource.get();
        } else if (isQueryIndex(inputInfo)) {
            if (queryIndexStrategy.resolution(indexDataSource, inputInfo) != null) {
                return doIfError(ErrorDTO.INNER_DATASOURCE_ERR.getMsg());
            }
            DataSourcePO<List<Map<String, Object>>> rowKeyByMobileIndex = this.indexDataSource.get();
            Map<String, Object> rowKeyList = buildRowKeyList(rowKeyByMobileIndex.getResult());
            if (rowKeyList != null && !rowKeyList.isEmpty()) {
                inputInfo.addMust(rowKeyList);
            }
        }
        if (commonQueryStrategy.resolution(dataSource, inputInfo) != null) {
            return doIfError(ErrorDTO.INNER_DATASOURCE_ERR.getMsg());
        }
        return this.dataSource.get();
    }

    private boolean isQueryAddtionalData(DataCenterInputInfoDTO inputInfo) {
        return DETAILS.equals(String.valueOf(inputInfo.getMust().get(QUERY_TABLE)));
    }

    private boolean isQueryIndex(DataCenterInputInfoDTO inputInfo) {
        return QueryDimensionEnum.MOBILE.name().equals(inputInfo.getMust().get(QUERYDIMENSION));
    }

    private <T> void buildUpFinalResult(DataSourcePO<List<T>> result, DataSourcePO<List<T>> completionResult) throws Exception {
        if (completionResult == null) {
            return;
        }
        List<T> resultList = completionResult.getResult();
        resultList.addAll(result.getResult());
        result.setResult(resultList);
    }

    private Map<String, Object> buildRowKeyList(List<Map<String, Object>> indexInfo) {
        if (indexInfo == null || indexInfo.isEmpty()) {
            return null;
        }
        Map<String, Object> rowKeyMap = new HashMap<>();
        int cnt = 0;
        for (Map<String, Object> item : indexInfo) {
            Object rowKey = item.get(INDEX_FOREIGNKEY);
            if (rowKey == null) {
                continue;
            }
            rowKeyMap.put("rowKey" + cnt, rowKey);
            cnt++;
        }
        rowKeyMap.put(EXACT_QUERY, true);
        return rowKeyMap;
    }
}
