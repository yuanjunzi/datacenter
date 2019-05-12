package com.git.service.yuanjunzi.datacenter.domain.dto;

import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by wangshenxiang on 16/7/13.
 */
public class PassportDTO {
    private static final Logger LOGGER = LoggerFactory.getLogger(PassportDTO.class);

    private String _mt_datetime = "";
    private String user = "";
    private String email = "";
    private String source = "";
    private String ipStr = "";
    private String status = "";
    private String username = "";
    private String userid = "";
    private String err = "";
    private String auto_login = "";
    private String mobile = "";
    private String oldMobile = "";
    // 字符串手机号
    private String strMobile = "";
    private String cityid = "";
    private String appusername = "";
    private String appuserid = "";
    private String appusertype = "";
    private String subscribe = "";
    private String useragent = "";
    private String _c_mobile_uuid = "";
    private String utm_campaign = "";
    private String utm_medium = "";
    private String utm_term = "";
    private String utm_source = "";
    private String utm_content = "";
    private String platformid = ""; // 用户platform字段
    private String platform = "";   // 风控platform字段
    private String logintype = "";
    private String signuptype = "";
    private String type = "";
    private String unixtime;
    private String action = "";
    private String errmsg = "";
    private String host = "";
    private String uuid = "";
    private String referid;
    private String appnm = "";
    private String partner = "";
    private String fingerprint = "";
    private String extraFingerPrint = "";
    private String h5Fingerprint = "";
    // 微信小程序指纹
    private String wechatFingerprint = "";
    private String us = "";
    private String isNeedRegRefer = "";
    private String data = "";
    private Map<String, String> dataMap;
    private NetSafeDTO netSafeDTO;
    private String sdkVersion = "";
    // 登录业务类型0非活动（默认）、1活动
    private String loginSource = "";
    // 风控分配的活动平台ID
    private String campaignPlatform = "";
    // 活动ID
    private String allCampaignId = "";

    private void parseData() {
        if (data == null) {
            return;
        }

        dataMap = JSONUtils.fromJson(data, new TypeToken<Map<String, String>>() {
        }.getType());

        if (dataMap != null && dataMap.containsKey("netSafeDTO")) {
            netSafeDTO = JSONUtils.fromJson(dataMap.get("netSafeDTO"), NetSafeDTO.class);
        }
    }

    public static PassportDTO parsePassportMessage(String json) {
        PassportDTO passportDTO = JSONUtils.fromJson(json, PassportDTO.class);
        if (passportDTO == null) {
            LOGGER.error("parse msg err, json={}", json);
            return null;
        }
        passportDTO.parseData();
        return passportDTO;
    }

    public String getStrMobile() {
        return StringUtils.isBlank(strMobile) ? mobile : strMobile;
    }

    public void setStrMobile(String strMobile) {
        this.strMobile = strMobile;
    }

    @Override
    public String toString() {
        return "PassportDTO{" +
                "_mt_datetime='" + _mt_datetime + '\'' +
                ", user='" + user + '\'' +
                ", email='" + email + '\'' +
                ", source='" + source + '\'' +
                ", ipStr='" + ipStr + '\'' +
                ", status='" + status + '\'' +
                ", username='" + username + '\'' +
                ", userid=" + userid +
                ", err='" + err + '\'' +
                ", auto_login=" + auto_login +
                ", mobile=" + mobile +
                ", oldMobile=" + oldMobile +
                ", strMobile='" + strMobile + '\'' +
                ", cityid=" + cityid +
                ", appusername='" + appusername + '\'' +
                ", appuserid='" + appuserid + '\'' +
                ", appusertype='" + appusertype + '\'' +
                ", subscribe=" + subscribe +
                ", useragent='" + useragent + '\'' +
                ", _c_mobile_uuid='" + _c_mobile_uuid + '\'' +
                ", utm_campaign='" + utm_campaign + '\'' +
                ", utm_medium='" + utm_medium + '\'' +
                ", utm_term='" + utm_term + '\'' +
                ", utm_source='" + utm_source + '\'' +
                ", utm_content='" + utm_content + '\'' +
                ", platformid=" + platformid +
                ", platform=" + platform +
                ", logintype=" + logintype +
                ", signuptype=" + signuptype +
                ", type='" + type + '\'' +
                ", unixtime=" + unixtime +
                ", action='" + action + '\'' +
                ", errmsg='" + errmsg + '\'' +
                ", host='" + host + '\'' +
                ", uuid='" + uuid + '\'' +
                ", referid=" + referid +
                ", appnm='" + appnm + '\'' +
                ", partner=" + partner +
                ", fingerprint='" + fingerprint + '\'' +
                ", extraFingerPrint='" + extraFingerPrint + '\'' +
                ", h5Fingerprint='" + h5Fingerprint + '\'' +
                ", wechatFingerprint='" + wechatFingerprint + '\'' +
                ", us='" + us + '\'' +
                ", isNeedRegRefer='" + isNeedRegRefer + '\'' +
                ", data='" + data + '\'' +
                ", dataMap=" + dataMap +
                ", netSafeDTO=" + netSafeDTO +
                ", sdkVersion='" + sdkVersion + '\'' +
                ", loginSource=" + loginSource +
                ", campaignPlatform=" + campaignPlatform +
                ", allCampaignId=" + allCampaignId +
                '}';
    }

    public NetSafeDTO getNetSafeDTO() {
        return netSafeDTO;
    }

    public boolean isSuccess() {
        return StringUtils.equalsIgnoreCase(getStatus(), "success");
    }

    public String getRiskResult() {
        return String.valueOf(isSuccess() ? 1 : 2);
    }

    public String getRiskFailReason() {
        return StringUtils.trimToEmpty(isSuccess() ? StringUtils.EMPTY : getErr());
    }

    public String getFromData(String key) {
        if (dataMap == null || !dataMap.containsKey(key)) {
            return null;
        }
        return dataMap.get(key);
    }

    public String get_mt_datetime() {
        return _mt_datetime;
    }

    public void set_mt_datetime(String _mt_datetime) {
        this._mt_datetime = _mt_datetime;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getLogintype() {
        return logintype;
    }

    public void setLogintype(String logintype) {
        this.logintype = logintype;
    }

    public String getSignuptype() {
        return signuptype;
    }

    public void setSignuptype(String signuptype) {
        this.signuptype = signuptype;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getIpStr() {
        return ipStr;
    }

    public void setIpStr(String ipStr) {
        this.ipStr = ipStr;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getErr() {
        return err;
    }

    public void setErr(String err) {
        this.err = err;
    }

    public String getAuto_login() {
        return auto_login;
    }

    public void setAuto_login(String auto_login) {
        this.auto_login = auto_login;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getOldMobile() {
        return oldMobile;
    }

    public void setOldMobile(String oldMobile) {
        this.oldMobile = oldMobile;
    }

    public String getCityid() {
        return cityid;
    }

    public void setCityid(String cityid) {
        this.cityid = cityid;
    }

    public String getAppusername() {
        return appusername;
    }

    public void setAppusername(String appusername) {
        this.appusername = appusername;
    }

    public String getAppuserid() {
        return appuserid;
    }

    public void setAppuserid(String appuserid) {
        this.appuserid = appuserid;
    }

    public String getAppusertype() {
        return appusertype;
    }

    public void setAppusertype(String appusertype) {
        this.appusertype = appusertype;
    }

    public String getSubscribe() {
        return subscribe;
    }

    public void setSubscribe(String subscribe) {
        this.subscribe = subscribe;
    }

    public String getUseragent() {
        return useragent;
    }

    public void setUseragent(String useragent) {
        this.useragent = useragent;
    }

    public String get_c_mobile_uuid() {
        return _c_mobile_uuid;
    }

    public void set_c_mobile_uuid(String _c_mobile_uuid) {
        this._c_mobile_uuid = _c_mobile_uuid;
    }

    public String getUtm_campaign() {
        return utm_campaign;
    }

    public void setUtm_campaign(String utm_campaign) {
        this.utm_campaign = utm_campaign;
    }

    public String getUtm_medium() {
        return utm_medium;
    }

    public void setUtm_medium(String utm_medium) {
        this.utm_medium = utm_medium;
    }

    public String getUtm_term() {
        return utm_term;
    }

    public void setUtm_term(String utm_term) {
        this.utm_term = utm_term;
    }

    public String getUtm_source() {
        return utm_source;
    }

    public void setUtm_source(String utm_source) {
        this.utm_source = utm_source;
    }

    public String getUtm_content() {
        return utm_content;
    }

    public void setUtm_content(String utm_content) {
        this.utm_content = utm_content;
    }

    public String getPlatformid() {
        return platformid;
    }

    public void setPlatformid(String platformid) {
        this.platformid = platformid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUnixtime() {
        return unixtime;
    }

    public void setUnixtime(String unixtime) {
        this.unixtime = unixtime;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getReferid() {
        return referid;
    }

    public void setReferid(String referid) {
        this.referid = referid;
    }

    public String getAppnm() {
        return appnm;
    }

    public void setAppnm(String appnm) {
        this.appnm = appnm;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public String getExtraFingerPrint() {
        return extraFingerPrint;
    }

    public void setExtraFingerPrint(String extraFingerPrint) {
        this.extraFingerPrint = extraFingerPrint;
    }

    public String getH5Fingerprint() {
        return h5Fingerprint;
    }

    public void setH5Fingerprint(String h5Fingerprint) {
        this.h5Fingerprint = h5Fingerprint;
    }

    public String getWechatFingerprint() {
        return wechatFingerprint;
    }

    public void setWechatFingerprint(String wechatFingerprint) {
        this.wechatFingerprint = wechatFingerprint;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public String getUs() {
        return us;
    }

    public void setUs(String us) {
        this.us = us;
    }

    public String getIsNeedRegRefer() {
        return isNeedRegRefer;
    }

    public void setIsNeedRegRefer(String isNeedRegRefer) {
        this.isNeedRegRefer = isNeedRegRefer;
    }

    public String getSdkVersion() {
        return sdkVersion;
    }

    public void setSdkVersion(String sdkVersion) {
        this.sdkVersion = sdkVersion;
    }

    public String getLoginSource() {
        return loginSource;
    }

    public void setLoginSource(String loginSource) {
        this.loginSource = loginSource;
    }

    public String getCampaignPlatform() {
        return campaignPlatform;
    }

    public void setCampaignPlatform(String campaignPlatform) {
        this.campaignPlatform = campaignPlatform;
    }

    public String getAllCampaignId() {
        return allCampaignId;
    }

    public void setAllCampaignId(String allCampaignId) {
        this.allCampaignId = allCampaignId;
    }
}
