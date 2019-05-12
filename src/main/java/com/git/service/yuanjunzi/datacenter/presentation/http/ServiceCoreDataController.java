package com.git.service.yuanjunzi.datacenter.presentation.http;


import com.alibaba.fastjson.JSONObject;
import com.git.service.yuanjunzi.datacenter.application.service.DataProcessFacade;
import com.git.service.yuanjunzi.datacenter.application.service.DataProcessing;
import com.git.service.yuanjunzi.datacenter.application.service.TranslationStrategy;
import com.git.service.yuanjunzi.datacenter.application.task.PassportMonitorTask;
import com.git.service.yuanjunzi.datacenter.constants.*;
import com.git.service.yuanjunzi.datacenter.domain.MonitorTemplate;
import com.git.service.yuanjunzi.datacenter.domain.QueryTemplate;
import com.git.service.yuanjunzi.datacenter.domain.TemplateTranslator;
import com.git.service.yuanjunzi.datacenter.domain.factory.AbstractUsableDataFactory;
import com.git.service.yuanjunzi.datacenter.infrastructure.aspect.SafeOperateLogType;
import com.git.service.yuanjunzi.datacenter.infrastructure.utils.ParseUtils;
import com.git.service.yuanjunzi.datacenter.presentation.vo.DataCenterInputInfoVO;
import com.git.service.yuanjunzi.datacenter.presentation.vo.DataCenterVisualDataVO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.Map;

import static com.git.service.yuanjunzi.datacenter.application.service.DataProcessFacade.ONLY_QUERY;
import static com.git.service.yuanjunzi.datacenter.application.service.DataProcessFacade.STATISTIC_AND_CALCULATE_RATIO;
import static com.git.service.yuanjunzi.datacenter.domain.converter.DataCenterInputInfoConverter.*;


/**
 * Created by yuanjunzi on 2018/11/5.
 * 用户中心数据平台后端查询展示层层入口。负责数据展示和用户输入解析
 */
@Controller
@RequestMapping(value = "/service")
public class ServiceCoreDataController {

    @Resource(name = "fullUsableDataFactory")
    private AbstractUsableDataFactory fullUsableDataFactory;

    @Resource(name = "shortTermUsableDataFactory")
    private AbstractUsableDataFactory shortTermUsableDataFactory;

    @Resource(name = "passportMonitorTask")
    private PassportMonitorTask passportMonitorTask;

    @Resource(name = "queryTemplateTranslation")
    private TranslationStrategy queryTemplateTranslation;

    @RequestMapping(value = "/admin/data/query", method = RequestMethod.GET)
    public ModelAndView coreDataQuery() {
        ModelAndView mav = new ModelAndView();
        mav.addObject("criticalTerms", CriticalLogTermsEnum.toList());
        mav.addObject("statisticTypeList", StatisticEnum.toList());
        mav.addObject("queryTemplateList", QueryTemplate.getInstance().keySet());
        return mav;
    }

    @RequestMapping(value = "/admin/data/query", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject coreDataQueryOnly(@RequestBody Map<String, String> req) {
        TemplateTranslator translator = new TemplateTranslator(queryTemplateTranslation);
        translator.translate(req, QueryTemplate.getInstance(), req.get(QUERYTEMPLATE));
        int operationType = ParseUtils.parseInt(req.get(OPERATIONTYPE), 0);
        if (req.isEmpty() || !DataProcessFacade.getDataProcessingMap().containsKey(operationType)) {
            return DataCenterVisualDataVO.doIfError(ErrorMsgEnum.PARAM_ERROR.getMessage()).getJsonObject();
        }
        DataProcessing dataProcessing = DataProcessFacade.getDataProcessingMap().get(operationType);
        if (STATISTIC_AND_CALCULATE_RATIO.equals(operationType)) {
            return dataProcessing.specificTreatment(new DataCenterInputInfoVO(req), shortTermUsableDataFactory.createUsableData(), shortTermUsableDataFactory.createUsableData()).getJsonObject();
        }
        return dataProcessing.specificTreatment(new DataCenterInputInfoVO(req), shortTermUsableDataFactory.createUsableData()).getJsonObject();
    }

    @RequestMapping(value = "/admin/data/fulllogquery", method = RequestMethod.GET)
    @SafeOperateLogType(logType = {CommonConstant.LOG_TYPE_JUMP}, summary = "跳转全量数据平台", action = "跳转", buttonName = "跳转全量数据平台")
    public ModelAndView fullLogQuery() {
        ModelAndView mav = new ModelAndView();
        mav.addObject("queryDimensionList", QueryDimensionEnum.getQueryDimension());
        mav.addObject("actionNameList", ActionEnum.getActionNameList());
        return mav;
    }

    @RequestMapping(value = "/admin/data/fulllogquery", method = RequestMethod.POST)
    @SafeOperateLogType(logType = {CommonConstant.LOG_TYPE_SU}, summary = "跳转全量数据平台", action = "查询", buttonName = "查询全量数据", secretType = "查询全量日志数据")
    @ResponseBody
    public JSONObject fullLogQuery(@RequestBody Map<String, String> req) {
        DataProcessing dataProcessing = DataProcessFacade.getDataProcessingMap().get(ONLY_QUERY);
        if (dataProcessing == null) {
            return DataCenterVisualDataVO.doIfError(ErrorMsgEnum.PARAM_ERROR.getMessage()).getJsonObject();
        }
        return dataProcessing.specificTreatment(new DataCenterInputInfoVO(req), fullUsableDataFactory.createUsableData()).getJsonObject();
    }

    @RequestMapping(value = "/admin/data/monitor", method = RequestMethod.GET)
    @SafeOperateLogType(logType = {CommonConstant.LOG_TYPE_JUMP}, summary = "跳转passport监控页面", action = "跳转", buttonName = "passport监控页面")
    public ModelAndView passportMonitor() {
        ModelAndView mav = new ModelAndView();
        mav.addObject("monitorTemplateList", MonitorTemplate.getInstance().keySet());
        return mav;
    }

    @RequestMapping(value = "/admin/data/monitor", method = RequestMethod.POST)
    @SafeOperateLogType(logType = {CommonConstant.LOG_TYPE_SU}, summary = "查询passport监控", action = "查询", buttonName = "passport监控查询", secretType = "passport监控")
    @ResponseBody
    public JSONObject passportMonitorQuery(@RequestBody Map<String, String> req) {
        DataCenterVisualDataVO result = passportMonitorTask.getMonitorMap().get(req.get(MONITORTEMPLATE));
        if (result == null || result.getJsonObject() == null) {
            return DataCenterVisualDataVO.doIfError(ErrorMsgEnum.PARAM_ERROR.getMessage()).getJsonObject();
        }
        return result.getJsonObject();
    }
}
