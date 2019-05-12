package com.git.service.yuanjunzi.datacenter.application.task;

import cn.hutool.core.thread.NamedThreadFactory;
import com.git.service.yuanjunzi.datacenter.application.service.DataProcessFacade;
import com.git.service.yuanjunzi.datacenter.application.service.DataProcessing;
import com.git.service.yuanjunzi.datacenter.application.service.QueryTemplateTranslation;
import com.git.service.yuanjunzi.datacenter.domain.MonitorTemplate;
import com.git.service.yuanjunzi.datacenter.domain.TemplateTranslator;
import com.git.service.yuanjunzi.datacenter.domain.dto.QueryConditionDTO;
import com.git.service.yuanjunzi.datacenter.infrastructure.utils.JSONUtils;
import com.git.service.yuanjunzi.datacenter.infrastructure.utils.ParseUtils;
import com.git.service.yuanjunzi.datacenter.presentation.vo.DataCenterInputInfoVO;
import com.git.service.yuanjunzi.datacenter.presentation.vo.DataCenterVisualDataVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.git.service.yuanjunzi.datacenter.application.service.DataProcessFacade.STATISTIC_AND_CALCULATE_RATIO;
import static com.git.service.yuanjunzi.datacenter.domain.converter.DataCenterInputInfoConverter.MUSTCONDITION;
import static com.git.service.yuanjunzi.datacenter.domain.converter.DataCenterInputInfoConverter.OPERATIONTYPE;
import static com.git.service.yuanjunzi.datacenter.domain.repository.DataCenterParamResolutionStrategy.QUERY_LAST_ONE_HOUR;
import static com.git.service.yuanjunzi.datacenter.domain.repository.DataCenterParamResolutionStrategy.QUERY_LAST_ONE_HOUR_LAST_WEEK;


/**
 * Created by yuanjunzi on 2019/3/5.
 * 定时统计各类业务关键指标并存储在内存中
 */
@Component("passportMonitorTask")
@Slf4j
public class PassportMonitorTask extends AbstractMonitorTask {

    private static final ThreadPoolExecutor PASSPORT_MONITOR_EXECUTOR = new ThreadPoolExecutor(
            16,
            32,
            30,
            TimeUnit.MINUTES,
            new ArrayBlockingQueue<>(16),
            new NamedThreadFactory("passportMonitorThread", true),
            new ThreadPoolExecutor.AbortPolicy()
    );

    private final static String[] CONTEXT_TYPE = {QUERY_LAST_ONE_HOUR, QUERY_LAST_ONE_HOUR_LAST_WEEK};

    /**
     * 同时存放本周和上周的数据，同时更新，保证数据一致性
     */
    private volatile static Map<String, DataCenterVisualDataVO> monitorMap = new HashMap<>();

    @Resource(name = "queryTemplateTranslation")
    private QueryTemplateTranslation queryTemplateTranslation;

    @Override
    @Scheduled(cron = "0 0/1 * * * ?")
    public void monitor() {
        Map<String, Map<String, Map<String, String>>> monitorTemplateMap = MonitorTemplate.getInstance();
        Map<String, Future<DataCenterVisualDataVO>> futureResultMap = new HashMap<>();
        for (String contextType : CONTEXT_TYPE) {
            String timeLimit = getTimeLimit(contextType);
            for (Map.Entry<String, Map<String, Map<String, String>>> entry : monitorTemplateMap.entrySet()) {
                Map<String, String> req = new HashMap<>();
                req.put(MUSTCONDITION, timeLimit);
                TemplateTranslator translator = new TemplateTranslator(queryTemplateTranslation);
                translator.translate(req, monitorTemplateMap, entry.getKey());
                int operationType = ParseUtils.parseInt(req.get(OPERATIONTYPE), 0);
                if (!DataProcessFacade.getDataProcessingMap().containsKey(operationType)) {
                    continue;
                }
                DataProcessing dataProcessing = DataProcessFacade.getDataProcessingMap().get(operationType);
                if (STATISTIC_AND_CALCULATE_RATIO.equals(operationType)) {
                    Future<DataCenterVisualDataVO> result = PASSPORT_MONITOR_EXECUTOR.submit(() -> dataProcessing.specificTreatment(
                            new DataCenterInputInfoVO(req)
                            , shortTermUsableDataFactory.createUsableData()
                            , shortTermUsableDataFactory.createUsableData()
                    ));
                    futureResultMap.put(entry.getKey() + contextType, result);
                    continue;
                }
                Future<DataCenterVisualDataVO> result = PASSPORT_MONITOR_EXECUTOR.submit(() -> dataProcessing.specificTreatment(
                        new DataCenterInputInfoVO(req)
                        , shortTermUsableDataFactory.createUsableData())
                );
                futureResultMap.put(entry.getKey() + contextType, result);
            }
        }
        Map<String, DataCenterVisualDataVO> tempMonitorMap = new HashMap<>();
        for (Map.Entry<String, Future<DataCenterVisualDataVO>> item : futureResultMap.entrySet()) {
            try {
                DataCenterVisualDataVO vo = item.getValue().get(18, TimeUnit.SECONDS);
                if (vo != null) {
                    tempMonitorMap.put(item.getKey(), vo);
                }
            } catch (Exception e) {
                log.error("passportMonitor error! exception={}", e.getMessage(), e);
            }
        }
        if (!tempMonitorMap.isEmpty()) {
            monitorMap = tempMonitorMap;
        }
    }

    @Override
    public Map<String, DataCenterVisualDataVO> getMonitorMap() {
        return monitorMap;
    }

    private String getTimeLimit(String contextType) {
        QueryConditionDTO queryCondition = new QueryConditionDTO();
        queryCondition.setTerm(contextType);
        queryCondition.setValue("true");
        List<QueryConditionDTO> list = new ArrayList<>();
        list.add(queryCondition);
        return JSONUtils.toJson(list);
    }
}
