package com.git.service.yuanjunzi.datacenter.infrastructure.dao.po;

import com.git.service.yuanjunzi.datacenter.constants.ActionEnum;
import com.git.service.yuanjunzi.datacenter.constants.CriticalLogTermsEnum;
import com.git.service.yuanjunzi.datacenter.constants.RiskParamEnum;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;

import static com.git.service.yuanjunzi.datacenter.constants.CriticalLogTermsEnum.*;

/**
 * Created by yuanjunzi on 2018/11/26.
 * 数据平台数据持久化对象
 */
public class DataSourcePO<T> {

    private T result;

    private List<String> title;

    private Integer type;

    private boolean success;

    private CriticalLogTermsEnum titleType;

    private String errMsg;

    private volatile static Map<CriticalLogTermsEnum, Map<String, String>> criticalTermsTransform
            = ImmutableMap.<CriticalLogTermsEnum, Map<String, String>>builder()
            .put(ACTION, ActionEnum.getActionToNameMap())
            .put(PLATFORM, RiskParamEnum.PLATFORM.getContent())
            .put(PARTNER, RiskParamEnum.PARTNER.getContent())
            .put(APPNM, RiskParamEnum.APPNM.getContent())
            .build();

    public static <T> DataSourcePO<T> doIfError(String errMsg) {
        DataSourcePO<T> errResult = new DataSourcePO<>();
        errResult.setErrMsg(errMsg);
        return errResult;
    }

    public void titleTransfer() {
        Map<String, String> map = criticalTermsTransform.get(this.titleType);
        if (map == null || map.isEmpty()) {
            return;
        }
        for (int cnt = 1; cnt < this.title.size(); cnt++) {
            String des = map.get(this.title.get(cnt));
            if (StringUtils.isNotEmpty(des)) {
                this.title.set(cnt, des);
            }
        }
    }

    public CriticalLogTermsEnum getTitleType() {
        return titleType;
    }

    public void setTitleType(CriticalLogTermsEnum titleType) {
        this.titleType = titleType;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public List<String> getTitle() {
        return title;
    }


    public void setTitle(List<String> title) {
        this.title = title;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
