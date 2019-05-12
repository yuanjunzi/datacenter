package com.git.service.yuanjunzi.datacenter.presentation.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SdkJoinVO {
    private int id;
    private int sdkType;
    private String pmMis;
    private String rdMis;
    private String productName;
    private String productDesc;
    private String applicantMis;
    private String applicantOrgInfo;
    private int status;
    private int riskApp;
    private int riskPlatform;
    private int riskPartner;
    private String appPackageName;
    private String appName;
    private String wechatAppId;
    private String wechatAppSecret;
    private String specialRiskParam;
    private String auditMis;
    private String rejectReason;
    private String joinKey;
    private int cretime;
    private int modtime;
}
