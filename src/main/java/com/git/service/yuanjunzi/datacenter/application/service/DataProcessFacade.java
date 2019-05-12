package com.git.service.yuanjunzi.datacenter.application.service;

import cn.hutool.core.thread.NamedThreadFactory;
import com.git.service.yuanjunzi.datacenter.constants.CriticalLogTermsEnum;
import com.git.service.yuanjunzi.datacenter.constants.ErrorMsgEnum;
import com.git.service.yuanjunzi.datacenter.domain.UsableDataRepository;
import com.git.service.yuanjunzi.datacenter.domain.converter.Converter;
import com.git.service.yuanjunzi.datacenter.domain.converter.DataCenterInputInfoConverter;
import com.git.service.yuanjunzi.datacenter.domain.converter.DataCenterOutputInfoConverter;
import com.git.service.yuanjunzi.datacenter.domain.dto.DataCenterInputInfoDTO;
import com.git.service.yuanjunzi.datacenter.infrastructure.dao.po.DataSourcePO;
import com.git.service.yuanjunzi.datacenter.presentation.vo.DataCenterInputInfoVO;
import com.git.service.yuanjunzi.datacenter.presentation.vo.DataCenterVisualDataVO;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang.StringUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static com.git.service.yuanjunzi.datacenter.application.service.QueryTemplateTranslation.DENOMINATOR;
import static com.git.service.yuanjunzi.datacenter.application.service.QueryTemplateTranslation.NUMERATOR;
import static com.git.service.yuanjunzi.datacenter.infrastructure.MccManager.RATIO_CALCULATE_TIMEOUT;

/**
 * Created by yuanjunzi on 2018/12/18.
 * 外观模式变种，将查询数据、数据拼装、入参转换等模块拼装起来。
 * 与经典外观模式相比 优势在于无需创建对象，且接口放在map中 上层使用无需if-else
 */
public class DataProcessFacade {

    private volatile static Map<Integer, DataProcessing> dataProcessingMap;

    private static final ThreadPoolExecutor CALCULATE_RATIO_EXECUTOR = new ThreadPoolExecutor(
            8,
            16,
            30,
            TimeUnit.MINUTES,
            new ArrayBlockingQueue<>(16),
            new NamedThreadFactory("calculateRatioThread", true),
            new ThreadPoolExecutor.AbortPolicy()
    );

    public static final Integer ONLY_QUERY = 1;

    public static final Integer QUERY_DETAIL_OR_STATISTIC = 2;

    public static final Integer STATISTIC_AND_CALCULATE_RATIO = 3;

    public static Map<Integer, DataProcessing> getDataProcessingMap() {
        if (dataProcessingMap == null) {
            synchronized (DataProcessFacade.class) {
                if (dataProcessingMap == null) {
                    dataProcessingMap = ImmutableMap.<Integer, DataProcessing>builder()
                            .put(ONLY_QUERY, (DataCenterInputInfoVO inputInfo, UsableDataRepository... usableDatumRepositories) -> {
                                if (usableDatumRepositories.length < 1) {
                                    return DataCenterVisualDataVO.doIfError(ErrorMsgEnum.UNKNOWN.getMessage());
                                }
                                Converter<DataCenterInputInfoVO, DataCenterInputInfoDTO> converter = new DataCenterInputInfoConverter();
                                DataSourcePO result = usableDatumRepositories[0].getWithCondition(converter.convertToSecondOne(inputInfo));
                                Converter<DataCenterVisualDataVO, DataSourcePO> converter2 = new DataCenterOutputInfoConverter();
                                return converter2.convertToFirstOne(result);
                            })
                            .put(QUERY_DETAIL_OR_STATISTIC, (DataCenterInputInfoVO inputInfo, UsableDataRepository... usableDatumRepositories) -> {
                                if (usableDatumRepositories.length < 1) {
                                    return DataCenterVisualDataVO.doIfError(ErrorMsgEnum.UNKNOWN.getMessage());
                                }
                                DataSourcePO<List<List<String>>> dataCenterQueryResult = statistic("", inputInfo, usableDatumRepositories[0]);
                                dataCenterQueryResult.titleTransfer();
                                Converter<DataCenterVisualDataVO, DataSourcePO> converter2 = new DataCenterOutputInfoConverter();
                                return converter2.convertToFirstOne(dataCenterQueryResult);
                            })
                            .put(STATISTIC_AND_CALCULATE_RATIO, (DataCenterInputInfoVO inputInfo, UsableDataRepository... usableDatumRepositories) -> {
                                if (usableDatumRepositories.length < 2) {
                                    return DataCenterVisualDataVO.doIfError(ErrorMsgEnum.UNKNOWN.getMessage());
                                }
                                Future<DataSourcePO<List<List<String>>>> futureNumerator
                                        = CALCULATE_RATIO_EXECUTOR.submit(
                                        () -> statistic(NUMERATOR, inputInfo, usableDatumRepositories[0])
                                );
                                Future<DataSourcePO<List<List<String>>>> futureDenominator
                                        = CALCULATE_RATIO_EXECUTOR.submit(
                                        () -> statistic(DENOMINATOR, inputInfo, usableDatumRepositories[1])
                                );
                                DataSourcePO<List<List<String>>> numeratorResult;
                                DataSourcePO<List<List<String>>> denominatorResult;
                                try {
                                    numeratorResult = futureNumerator.get(RATIO_CALCULATE_TIMEOUT, TimeUnit.SECONDS);
                                    denominatorResult = futureDenominator.get(RATIO_CALCULATE_TIMEOUT, TimeUnit.SECONDS);
                                } catch (TimeoutException e) {
                                    return DataCenterVisualDataVO.doIfError("请求超时，请重试");
                                } catch (Exception e) {
                                    return DataCenterVisualDataVO.doIfError(ErrorMsgEnum.SERVER_BUSY.getMessage());
                                }
                                if (numeratorResult == null || denominatorResult == null
                                        || numeratorResult.getResult() == null || denominatorResult.getResult() == null) {
                                    return DataCenterVisualDataVO.doIfError(ErrorMsgEnum.UNKNOWN.getMessage());
                                }
                                DataSourcePO<List<List<String>>> finalResult = calculateRatio(numeratorResult, denominatorResult);
                                Converter<DataCenterVisualDataVO, DataSourcePO> converter2 = new DataCenterOutputInfoConverter();
                                return converter2.convertToFirstOne(finalResult);
                            })
                            .build();
                }
            }
        }
        return dataProcessingMap;
    }

    private static DataSourcePO<List<List<String>>> statistic(String position, DataCenterInputInfoVO inputInfo
            , UsableDataRepository usableDataRepository) {
        Converter<DataCenterInputInfoVO, DataCenterInputInfoDTO> converter = new DataCenterInputInfoConverter();
        inputInfo.setPosition(position);
        DataCenterInputInfoDTO dto = converter.convertToSecondOne(inputInfo);
        DataSourcePO<List<List<String>>> result = usableDataRepository.countWithCondition(dto);
        result.setTitleType(CriticalLogTermsEnum.valueOf(dto.getBucket().toUpperCase()));
        return result;
    }

    private static DataSourcePO<List<List<String>>> calculateRatio(DataSourcePO<List<List<String>>> numeratorResult
            , DataSourcePO<List<List<String>>> denominatorResult) {
        Map<String, Integer> denominatorTitleIndex = new HashMap<>();
        for (int i = 1; i < denominatorResult.getTitle().size(); i++) {
            denominatorTitleIndex.put(denominatorResult.getTitle().get(i), i);
        }
        List<List<String>> numeratorList = numeratorResult.getResult();
        List<List<String>> denominatorList = denominatorResult.getResult();
        List<List<String>> resultList = new ArrayList<>();
        DecimalFormat df = new DecimalFormat("0.000");
        for (int row = 0; row < numeratorList.size() && row < denominatorList.size(); row++) {
            List<String> resultRow = new ArrayList<>();
            resultRow.add(numeratorList.get(row).get(0));
            List<String> numeratorTitle = numeratorResult.getTitle();
            for (int col = 1; col < numeratorTitle.size(); col++) {
                double numerator = Double.parseDouble(numeratorList.get(row).get(col));
                String denominatorStr = denominatorList.get(row).get(denominatorTitleIndex.get(numeratorTitle.get(col)));
                if (StringUtils.isBlank(denominatorStr)) {
                    resultRow.add("1");
                    continue;
                }
                double denominator = Double.parseDouble(denominatorStr);
                if (denominator < 0.01) {
                    resultRow.add("1");
                    continue;
                }
                double calResult = numerator / denominator;
                resultRow.add(df.format(calResult));
            }
            resultList.add(resultRow);
        }

        numeratorResult.titleTransfer();
        numeratorResult.setResult(resultList);
        numeratorResult.setType(STATISTIC_AND_CALCULATE_RATIO);
        return numeratorResult;
    }
}
