package com.git.service.yuanjunzi.datacenter.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuanjunzi on 2019/1/11.
 */
public enum ErrorMsgEnum {
    UNKNOWN("未知错误"),
    DEFAULT("内部服务器错误"),
    SERVER_BUSY("服务器繁忙"),
    NETWORK_BUSY("网络繁忙"),
    PARAM_ERROR("参数错误"),
    C_USER_TOKEN_LOGIN_FAIL("登录失败"),
    C_USER_LOGIN_LOCKED_UNION("发现您的账号异常，请拨打客服电话10107888咨询解决"),
    C_USER_LOGIN_LOCKED_MOBILE("检测到您的账号存在安全隐患，客户端已将您的账号锁定"),
    C_USER_LOGIN_LOCKED_EMAIL("发现您的账号异常，已自动为您锁定账户"),
    C_USER_LOGIN_BANNED_MOBILE("您的账号存在安全隐患，请拨打客服电话10107888咨询解决"),
    C_USER_EMAIL_INVAL("您输入了错误的邮箱地址"),
    C_USER_LOGIN_PASSWORD_ERR("账号或密码错误，请重新输入"),
    C_USER_PASSWORD_NOT_RIGHT("当前密码错误，请重新输入"),
    C_USER_PASSWORD_NOT_RIGHT_V2("旧密码错误，请重新输入"),
    C_USER_MOBILE_NOT_VERIFIED("该账号缺少手机或邮箱作为登录凭证，为保护您的账户安全，请联系客服解决：10107888"),
    C_USER_EMAIL_NOT_VERIFIED("此邮箱尚未验证，请先到您的邮箱收信，并点击其中的链接验证您的邮箱"),
    C_USER_MOBILE_INVAL("手机号输入不正确，请重新输入"),
    C_USER_PASSWORD_NOT_EMPTY("请输入密码"),
    C_USER_PASSWORD_NOT_SAME("两次输入的密码不一致"),
    C_USER_PASSWORD_DUPLICATED("您曾经使用过这个密码，请重新设置密码"),
    C_USER_USERNAME_RESERVED("该用户名已被占用，另取一个用户名吧"),
    C_USER_USERNAME_FIRSTCHAR_ERR("用户名必须以英文字母或汉字开头"),
    C_USER_USERNAME_NULL("请输入用户名"),
    C_USER_USERNAME_TOO_SHORT("用户名太短，最少2个汉字或4个字符"),
    C_USER_USERNAME_TOO_LONG("用户名太长，最多16个字符或8个汉字"),
    C_USER_USERNAME_CHAR_ERR("用户名只能使用中文汉字、英文和数字"),
    C_USER_USERNAME_EXISTS("该用户名已被占用，另取一个用户名吧"),
    C_USER_FILE_TOO_MANY("最多只能上传一个文件"),
    C_USER_FILE_TOO_LARGE("上传文件过大"),
    C_USER_RESETPWD_TRY_LIMIT("抱歉，尝试次数过多，请24小时之后重试"),
    C_USER_RESETPWD_NEED_CAPTCHA("请输入验证码"),
    C_USER_RESETPWD_CAPTCHA_ERROR("验证码错误"),
    C_USER_REQ_RESETPWD_BANNED("您的账号存在安全隐患，请联系客服10107888进行修改"),
    C_BINDMOBILE_NEED_BIND("请先发送短信动态码"),
    C_BINDMOBILE_VERIFYCODE_ERR("短信验证码错误，请重试"),
    C_USER_LOGIN_NEED_CAPTCHA("请输入验证码"),
    C_USER_LOGIN_CAPTCHA_ERROR("验证码错误"),
    C_USER_TOKEN_INVALID("请重新登录"),
    C_USER_MOBILE_TRY_LIMIT("尝试次数过多，请24小时之后发码重试"),
    C_USER_MOBILE_CODE_ERR("手机验证码错误"),
    C_USER_PASSWORD_HAS_EXISTED("密码已存在"),
    C_USER_LOGIN_NOT_EXIST("账号不存在，请重新输入"),
    C_BINDMOBILE_NEED_LOGIN("请先登录"),
    C_BINDMOBILE_OLDMOBILE_ERR("已绑定手机号输入错误，请重新输入"),
    C_BINDMOBILE_BIND_BY_OTHERS_CONFIRM("该手机号已被其他账号绑定。如果继续，原账号将自动解绑。是否继续？"),
    C_BINDMOBILE_BINDED("您当前已经绑定该手机号"),
    C_BINDMOBILE_BINDED_OVERLIMIT("该手机号绑定次数过多，请换用其他手机号"),
    C_BINDMOBILE_SEND_LIMIT("短信验证码重试发送次数过多，请联系客服解决：10107888"),
    C_BINDMOBILE_NEED_VERIFY_OLD_MOBILE("请先验证手机号"),
    C_USER_SIGNUPNICK_NOT_OPEN("功能未开通，请使用手机号注册"),
    C_USER_SERVERLOGIN_CODE_ERR("发送短信动态码错误，请重新发送"),
    C_USER_SERVERLOGIN_NO_TOKEN("请发送短信后耐心等待"),
    C_USER_MOBILE_ALREADY_SIGNUP("该手机号已经注册，请直接登录或找回密码"),
    C_USER_MOBILE_ALREADY_SIGNUP_CHEAT("该手机号已注册，请返回上一页面进行“手机号快捷登录”"),
    USER_RISK_VERIFY_ERR("风险验证失败"),
    USER_RISK_NEED_MORE_VEIRFYINFO("还需要验证信息"),
    C_REBIND_NEED_CHECK_OLD("需要先完成之前的校验"),
    C_USER_NEED_VERIFYCODE("请输入验证码"),
    C_USER_PASSWORD_ALL_NUMERIC("密码不能全是数字，请重新输入"),
    C_USER_PASSWORD_ALL_ALPHA("密码不能全是字母，请重新输入"),
    C_USER_PASSWORD_ALL_CHARACTER("密码不能全是字符，请重新输入"),
    C_USER_PASSWORD_TOO_WEAK("密码设置不符合规范，请输入8~32位，含数字、字母、字符中2种或以上元素"),
    C_USER_INVOICE_CODE_ERR("验证码错误"),
    C_SMSTASK_MSG_RISK_DENIED_1M("操作过于频繁，请1分钟后重试"),
    C_SMSTASK_MSG_RISK_DENIED_24H("动态码获取次数过多，请24小时后重试"),
    C_SMSTASK_MSG_VERIFY_CAPTCHA("请输入验证码"),
    C_USER_CAPTCHA_ERROR("验证码错误"),
    C_USER_MOBILE_CODE_EXPIRED("手机动态码已过期，请重新获取"),
    C_SMSTASK_MSG_INVALID_CODE("动态码输入错误，请重新获取"),
    C_USER_MOBILE_CODE_ERROR("手机动态码错误，请重新输入"),
    C_USER_PASSWORD_NONE("您的账号还未设置密码，请切换到无账号快捷登录，登录后请设置密码"),
    C_USER_CONCURRENT_SIGNUP_MOBILE("该手机已经被注册，请立即登录"),
    C_USER_CONCURRENT_SIGNUP_EMAIL("该邮箱已经被注册，请立即登录"),
    C_SMSTASK_MSG_RISK_ERROR("系统繁忙，请稍后重试"),
    E_APP_NEED_UPGRADE("您的客户端版本太低，为保护账户安全，请升级到最新版"),
    C_USER_EMAIL_BINDED("该邮箱已注册"),
    C_MOBILE_ALREADY_BIND_USER("该手机号已经绑定其他账号"),
    C_USER_ALREADY_BIND_MOBILE("该账号已经绑定其他手机号"),
    C_EMAILUSER_VERIFY_OBJ_NOT_LOAD("抱歉，这个邮箱未被注册"),
    C_USER_MOBILE_CODE_FREQUENT("手机验证码发送过于频繁，请60秒后重试"),
    C_USER_MOBILE_CODE_LIMIT("获取手机验证码次数过多，请24小时之后重试"),
    C_USER_EMAIL_CODE_ERR("邮箱验证链接错误，请检查链接是否完整"),
    C_USER_EMAIL_VERIFY_BY_OTHER("邮箱验证失败，此邮箱地址已验证过"),
    C_USER_EMAIL_ALREADY_VERIFIED("已验证成功，请登录"),
    C_USER_PASSWORD_SAME_WITH_PAYHASH("登录密码和支付密码相同，请重新设置密码"),
    C_MPAY_HTTP_POST("网络错误"),
    C_USER_UPLOAD_AVATAR_ERR("头像上传出错，请稍后重试"),
    C_USER_SMS_CODE_NULL("手机验证码未发送或者已过期，请重新发送手机验证码"),
    C_USER_REQUEST_ERR("请求有误"),
    C_USER_MOBILE_NAME_EMAIL_NULL("请输入手机号、用户名或邮箱"),
    C_USER_RISK_DENY("请先验证个人信息"),
    C_USER_ACCESSTOKEN_ERR("第三方数据有误，请稍后再试"),
    C_USER_SERVERLOGIN_CODE_NULL("动态码不存在或者已过期，请重新操作"),
    C_USEREXINFO_LOST_PARAM("至少填写一项参数"),
    C_USEREXINFO_BIRTHDAY_FORMAT_ERR("日期错误"),
    C_USEREXINFO_BIRTHDAY_FUTURE_ERR("日期错误，不能设置未来的日期哦！"),
    C_USER_NOT_LOCK("用户未被锁定，无需解锁"),
    C_USER_UNLOCK_FAIL("解锁失败"),
    C_USER_NOT_FROZEN("用户未被冻结，无需解解冻"),
    C_USER_VERIFYCODE_LIMIT("短信验证码错误次数过多，请您24小时之后重新获取短信验证码!"),
    C_USER_UPLOAD_AVATAR_SIZE_ERR("头像文件过大，请重新选择"),
    C_USER_MOBILE_VIRTUAL_MODIFY("您好，由于您的帐号是在点评注册，请使用点评进行操作"),
    C_USER_HAS_RISK("风控拒绝"),
    C_USER_STATUS_RULE_CHECK_FAIL("状态规则检查不通过"),
    C_USER_RESETPWD_CODE_ERR("抱歉，验证链接错误，请检查链接是否完整或稍候重新申请"),
    C_USER_RESETPWD_CODE_EXPIRE("抱歉，验证链接已过期，请重新申请"),
    C_USER_BINDEMAIL_EMAIL_EXIST("该邮箱已存在"),
    C_USER_EMAIL_SEND_LIMIT("发送次数过多，请稍后重试"),
    C_CONNECT_BINDED_BY_OTHER_CONFIRM("手机号已与另外一个美团账号绑定，点击确认将手机号绑定至当前的美团账号"),
    C_CONNECT_NOT_BINDED_MOBILE("您尚未绑定手机号，出于安全考虑，请先绑定手机号，再操作解绑该第三方账号"),
    C_RELATION_NOT_IMPORTED("好友关系尚未导入"),
    C_CONNECT_BINDED_THIS_CONNECT_TYPE("当前美团账号已经绑定其他第三方账号，请在账号设置中更换绑定"),
    C_RELATION_IMPORTING("好友关系正在导入中"),
    C_RELATION_DISABLEUSERATTENTION_FAIL("取消关注未生效，请重新操作？"),
    C_RELATION_WEIXIN_SCOPE_ERR("请微信授权时勾选“允许好友看到你在该应用上的操作”"),
    C_RELATION_ENABLEUSERATTENTION_FAIL("恢复关注未生效，请重新操作？"),
    C_CONNECT_NOT_BIND_MOBILE("检测到您的账号还未绑定手机号，请绑定手机号"),
    C_CONNECT_NOT_LOGIN_SUCCESS("登录失效,请重新登录账号"),
    C_USER_LOGIN_NEED_VERIFY("检测到您的账号存在异常，请进行验证"),
    C_USER_LOGIN_VERIFY_FAILED("二次验证失败"),
    C_RELATION_IMPORT_PROCESSING("好友关系正在处理中"),
    C_RELATION_SWITCHOFF_LIMIT("停用次数过多,请明日再试"),
    C_WEAPP_INVALID_SESSION("会话已过期，请重新登录"),
    C_WECHAT_LOGIN_DETECTED("您的微信账号已绑定以上美团账号是否直接登录？"),
    C_USER_CANNOT_UNION("您的账号不满足升级条件"),
    C_USER_UNION_FAILED("账号升级失败"),
    C_USER_REALBIND_ERR("绑定失败"),
    C_USER_REALBIND_UNBINDMOBIE_ERR("解绑手机号错误"),
    C_USER_REALBIND_RELATION_ERR("实绑定对应关系错误"),
    C_USER_UNION_FAILED_NEED_BIND_MOBILE("您的账号未绑定手机号，无法升级"),
    C_WMREALBIND_MTUSERID_BINDED("mtUserId已被其他dpUserId绑定"),
    C_WMREALBIND_DPUSERID_BINDED("dpUserId已被其他mtUserId绑定"),
    C_USER_SWITCH_ACCOUNT("检测到手机号已被其他账号绑定，请选择需要登录的账号"),
    C_DPUSER_NOT_BIND_MOBILE("检测到该点评账号没有绑定手机号"),
    C_USER_LOGIN_YODA_VERIFY("需要风控验证"),
    C_MOBILE_COUNTRY_NOT_SUPPORTED("暂不支持境外/港澳台号码"),
    C_MTID_NOT_EXIST_OR_NOT_BIND("当前美团ID不存在或未进行实绑定"),
    C_USER_CURRENT_PASSWORD_TOO_WEAK("检测到您当前密码安全性很低，账号存在风险。需要立即更换密码以确保账号安全"),
    C_CONNECT_BIND_USERID_ALREADY_BINDED("当前userid已存在绑定关系"),
    C_CONNECT_BIND_THIRDUSERID_ALREADY_BINDED("当前第三方id已存在绑定关系"),
    C_CONNECT_CONSTANT_CONFIG_MAP_ANALYSIS_ERR("第三方授权参数表解析失败"),
    C_CONNECT_CONSTANT_CONFIG_MAP_ID_NAME_ERR("第三方授权参数表ID命名不合法"),
    C_RESET_PASSWORD_CHECK_OLD("需要先完成之前的校验"),
    C_OPER_TOO_FAST_HOUR("操作过于频繁，请24小时后再操作"),
    C_DPUSERINFO_BY_DPTOKEN_ERR("获取点评用户信息失败"),
    C_THIRD_PARTY_USER_INFO_NOT_EXIST("当前查询的第三方用户信息不存在"),
    C_TAXI_ORDER_UNPAID("该手机号已绑定其他美团账号，且绑定的账号有打车订单未支付，请更换其他手机号"),
    C_FINANCE_ORDER_UNPAID("该手机号已绑定其他美团账号，且绑定的账号有金融产品未结清，请更换其他手机号"),
    C_FINANCE_AUTO_SIGNUP_LIMITED("接口调用总次数已达到限制值，请联系业务方"),
    C_SYSTEM_AVATAR_MAINTAINING("系统维护中，无法修改头像"),
    C_SYSTEM_NICKNAME_MAINTAINING("系统维护中，无法修改昵称"),
    C_USER_HAVE_ASSETS("用户有资产，不能融合"),
    C_QR_LOGIN_CODE_INVALID("二维码不存在或过期"),
    C_USER_CANCEL_CHECK_ERROR("无法注销账号"),
    C_USER_MOBILE_CHECK_ERROR("无法更换手机号"),
    C_USER_MB_CHECK_ERROR("摩拜传入的手机号错误");

    private String message;

    ErrorMsgEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    private static Map<String, String> errorMsgToNameMap;

    private static Map<String, String> nameToErrorMsgMap;

    static {
        errorMsgToNameMap = new HashMap<>();
        nameToErrorMsgMap = new HashMap<>();
        for (ErrorMsgEnum item : ErrorMsgEnum.values()) {
            errorMsgToNameMap.put(item.name(), item.getMessage());
            nameToErrorMsgMap.put(item.getMessage(), item.name());
        }
    }

    public static Map<String, String> getErrorMsgToNameMap() {
        return errorMsgToNameMap;
    }

    public static Map<String, String> getNameToErrorMsgMap() {
        return nameToErrorMsgMap;
    }
}
