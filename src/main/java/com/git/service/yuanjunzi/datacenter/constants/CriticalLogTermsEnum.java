package com.git.service.yuanjunzi.datacenter.constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanjunzi on 2018/11/5.
 */
public enum CriticalLogTermsEnum {
    TIME("mt_datetime"),
    USERID("userid"),
    UUID("uuid"),
    ACTION("action"),
    APPNAME("appName"),
    PLATFORM("platform"),
    PARTNER("partner"),
    APPNM("appnm"),
    MOBILE("mobile"),
    OLDMOBILE("oldMobile"),
    STATUS("status"),
    ERRMSG("errmsg"),
    SOURCE("source"),
    SIGNUPTYPE("signuptype"),
    LOGINTYPE("logintype"),
    IP("ip");

    private String term;

    private volatile static List<String> criticalLogTermsList;

    public static List<String> toList() {
        if (criticalLogTermsList == null) {
            synchronized (CriticalLogTermsEnum.class) {
                if (criticalLogTermsList == null) {
                    List<String> list = new ArrayList<>();
                    for (CriticalLogTermsEnum item : CriticalLogTermsEnum.values()) {
                        list.add(item.getTerm());
                    }
                    criticalLogTermsList = list;
                }
            }
        }
        return criticalLogTermsList;
    }

    CriticalLogTermsEnum(String term) {
        this.term = term;
    }

    public String getTerm() {
        return term;
    }


}
