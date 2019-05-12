package com.git.service.yuanjunzi.datacenter.domain.dto;

public class ErrorDTO {
    public static final ErrorDTO PARAM_ERR = new ErrorDTO(1001, "参数错误");
    public static final ErrorDTO NETWORK_ERR = new ErrorDTO(1002, "网络错误");
    public static final ErrorDTO SSO_ERR = new ErrorDTO(1003, "授权错误");
    public static final ErrorDTO DEFAULT_ERR = new ErrorDTO(1004, "未知错误");
    public static final ErrorDTO INNER_DATASOURCE_ERR = new ErrorDTO(1005, "内部数据源使用错误");

    public static final ErrorDTO BIZ_ERR_SIGNUP_APPLY_NO_REASON = new ErrorDTO(2001, "请填写注册原因");

    public static final ErrorDTO SEND_EMAIL_SENDER_ERR = new ErrorDTO(3001, "发件人账号/密码有误");
    public static final ErrorDTO SEND_EMAIL_NO_RECEIVER_ERR = new ErrorDTO(3002, "无可发送收件人");

    private int code;
    private String msg;

    public ErrorDTO(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static ErrorDTO cus(String msg) {
        return new ErrorDTO(1004, msg);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
