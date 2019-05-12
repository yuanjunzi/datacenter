package com.git.service.yuanjunzi.datacenter.presentation.vo;

import com.alibaba.fastjson.JSONObject;
import com.git.service.yuanjunzi.datacenter.domain.dto.CriticalLogTermsDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanjunzi on 2019/4/26.
 * 数据中心可视化数据
 */
public class DataCenterVisualDataVO {

    private JSONObject jsonObject;

    private List<CriticalLogTermsDTO> resultAboutLogin = new ArrayList<>();

    private List<CriticalLogTermsDTO> resultAboutSignUp = new ArrayList<>();

    private List<CriticalLogTermsDTO> resultAboutModifyUserInfo = new ArrayList<>();

    private List<CriticalLogTermsDTO> resultAboutUserStatusChange = new ArrayList<>();

    private List<CriticalLogTermsDTO> resultAboutUserMobileChange = new ArrayList<>();

    private String errMsg;

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public List<CriticalLogTermsDTO> getResultAboutLogin() {
        return resultAboutLogin;
    }

    public void setResultAboutLogin(List<CriticalLogTermsDTO> resultAboutLogin) {
        this.resultAboutLogin = resultAboutLogin;
    }

    public List<CriticalLogTermsDTO> getResultAboutSignUp() {
        return resultAboutSignUp;
    }

    public void setResultAboutSignUp(List<CriticalLogTermsDTO> resultAboutSignUp) {
        this.resultAboutSignUp = resultAboutSignUp;
    }

    public List<CriticalLogTermsDTO> getResultAboutModifyUserInfo() {
        return resultAboutModifyUserInfo;
    }

    public void setResultAboutModifyUserInfo(List<CriticalLogTermsDTO> resultAboutModifyUserInfo) {
        this.resultAboutModifyUserInfo = resultAboutModifyUserInfo;
    }

    public List<CriticalLogTermsDTO> getResultAboutUserStatusChange() {
        return resultAboutUserStatusChange;
    }

    public void setResultAboutUserStatusChange(List<CriticalLogTermsDTO> resultAboutUserStatusChange) {
        this.resultAboutUserStatusChange = resultAboutUserStatusChange;
    }

    public List<CriticalLogTermsDTO> getResultAboutUserMobileChange() {
        return resultAboutUserMobileChange;
    }

    public void setResultAboutUserMobileChange(List<CriticalLogTermsDTO> resultAboutUserMobileChange) {
        this.resultAboutUserMobileChange = resultAboutUserMobileChange;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public static DataCenterVisualDataVO doIfError(String errMsg) {
        DataCenterVisualDataVO errResult = new DataCenterVisualDataVO();
        errResult.setErrMsg(errMsg);
        return errResult;
    }
}
