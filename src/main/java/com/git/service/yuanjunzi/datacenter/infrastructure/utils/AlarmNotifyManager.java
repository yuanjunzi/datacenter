package com.git.service.yuanjunzi.datacenter.infrastructure.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by yuanjunzi on 2019/5/7.
 * 报警通知
 */
@Service
public class AlarmNotifyManager extends AbstractNotifyManager {

    @Value("${alarm.appKey}")
    public void setAlarmAppKey(String alarmAppKey) {
        super.appKey = alarmAppKey;
    }

    @Value("${alarm.appToken}")
    public void setAlarmAppToken(String alarmAppToken) {
        super.appToken = alarmAppToken;
    }

    @Value("${targetUrl}")
    public void setAlarmTargetUrl(String targetUrl) {
        super.targetUrl = targetUrl;
    }

    @Value("${alarm.fromUid}")
    public void setAlarmFromUid(long alarmFromUid) {
        super.fromUid = alarmFromUid;
    }
}
