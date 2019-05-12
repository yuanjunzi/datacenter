package com.git.service.yuanjunzi.datacenter.domain.dto;

/**
 * Created by yuanjunzi on 2018/11/7.
 */
public class ExtraLogTermsDTO {
    private String time;
    private String userId;
    private String uuid;
    private String ip;
    private String data;
    private String signuptype;
    private String logintype;
    private String useragent;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getSignuptype() {
        return signuptype;
    }

    public void setSignuptype(String signuptype) {
        this.signuptype = signuptype;
    }

    public String getLogintype() {
        return logintype;
    }

    public void setLogintype(String logintype) {
        this.logintype = logintype;
    }

    public String getUseragent() {
        return useragent;
    }

    public void setUseragent(String useragent) {
        this.useragent = useragent;
    }
}
