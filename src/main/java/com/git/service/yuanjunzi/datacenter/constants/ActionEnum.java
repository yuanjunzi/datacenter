package com.git.service.yuanjunzi.datacenter.constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yuanjunzi on 2018/11/26.
 */
public enum ActionEnum {
    APPLY("申请验证码"),
    SIGNUP("注册"),
    LOGIN("登录"),
    CODE("发送验码"),
    CHECK("风控校验"),
    SET_PASSWORD("设置密码"),
    SET_PASSWORD_STRONG("设置的密码强度高"),
    RESET_PASSWORD("修改密码"),
    RETRIEVE_PASSWORD("找回密码"),
    LOCK("锁定账号"),
    UNLOCK("解锁账号"),
    FROZEN("冻结账号"),
    UNFROZEN("解冻账号"),
    BAN("封禁账号"),
    UNBAN("解封账号"),
    CANCEL("系统注销"),
    UNCANCEL("解除注销"),
    UNBIND_MOBILE("解绑手机号"),
    REBIND_MOBILE("换绑手机号"),
    BIND_MOBILE("绑定手机号"),
    CHANGE_USERNAME("修改用户名"),
    AUDIT_USERNAME("审核用户名"),
    CHANGE_AVATAR("修改头像"),
    BIND_EMAIL("绑定邮箱"),
    REBIND_EMAIL("换绑邮箱"),
    UNBIND_EMAIL("解绑邮箱"),
    BIND_DIANPING("点评侧绑定手机号"),
    UNBIND_DIANPING("点评侧解绑手机号"),
    VIRTUALBIND_CHANGEMOBILE("点评侧换绑手机号同步到美团虚账号"),
    BIND_THIRD("绑定第三方账号"),
    UNBIND_THIRD("解绑第三方账号"),
    REFRESH_TOKEN("刷新登录态"),
    SET_STATUS("设置账号状态"),
    KICKOUT("踢出登录态"),
    LOGOUT("退出登录态"),
    SIGNUP_AUTO_REALBIND("注册后静默融合"),
    CANCEL_USER("用户自主注销账号");

    private String description;

    private static Map<String, String> actionToNameMap;

    private static Map<String, String> nameToActionMap;

    private static List<String> actionList;

    private static List<String> actionCodeList;

    static {
        actionToNameMap = new HashMap<>();
        nameToActionMap = new HashMap<>();
        actionList = new ArrayList<>();
        actionCodeList = new ArrayList<>();
        for (ActionEnum item : ActionEnum.values()) {
            actionToNameMap.put(item.name(), item.getDescription());
            nameToActionMap.put(item.getDescription(), item.name());
            actionList.add(item.getDescription());
            actionCodeList.add(item.name());
        }
    }

    ActionEnum(String d) {
        this.description = d;
    }

    public String getDescription() {
        return description;
    }

    public static Map<String, String> getActionToNameMap() {
        return actionToNameMap;
    }

    public static Map<String, String> getNameToActionMap() {
        return nameToActionMap;
    }

    public static List<String> getActionNameList() {
        return actionList;
    }

    public static List<String> getActionCodeList() {
        return actionCodeList;
    }
}
