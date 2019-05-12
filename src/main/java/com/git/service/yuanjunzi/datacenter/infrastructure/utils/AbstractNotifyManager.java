package com.git.service.yuanjunzi.datacenter.infrastructure.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Created by yuanjunzi on 2019/5/7.
 * 大象通知管理器
 */
@Service
@Slf4j
public class AbstractNotifyManager {

    private Pusher pusher;

    protected String appKey;

    protected String appToken;

    protected String targetUrl;

    protected Long fromUid;

    private Pusher getPusher() {
        if (pusher == null) {
            synchronized (NeiXinNotifyManager.class) {
                if (pusher == null) {
                    pusher = PusherBuilder.defaultBuilder()
                            .withAppkey(appKey)
                            .withApptoken(appToken)
                            .withTargetUrl(targetUrl)
                            .withFromUid(fromUid)
                            .build();
                }
            }
        }
        return pusher;
    }

    public boolean sendByMis(String mis, String content) {
        if (StringUtils.isBlank(mis)) {
            return false;
        }
        try {
            JSONObject result = getPusher().push(content, mis);
            return result.getIntValue("rescode") == 0;
        } catch (Exception e) {
            log.error("FailedToSendNeixin, {}, {}", mis, content, e);
            throw e;
        } finally {
            log.info("NeixinSendByMis:{}, {}", mis, content);
        }
    }
}
