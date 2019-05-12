package com.git.service.yuanjunzi.datacenter.presentation.vo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuanjunzi on 2019/4/24.
 * 数据平台前端传入参数
 */
public class DataCenterInputInfoVO {

    private Map<String, String> requestParam;

    private String position = "";

    public DataCenterInputInfoVO(Map<String, String> requestParam) {
        this.requestParam = requestParam;
    }

    public DataCenterInputInfoVO() {
        requestParam = new HashMap<>();
    }

    public Map<String, String> getRequestParam() {
        return requestParam;
    }

    public void setRequestParam(Map<String, String> requestParam) {
        this.requestParam = requestParam;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void addRequestParam(Map<String, String> requestParam) {
        this.requestParam.putAll(requestParam);
    }
}
