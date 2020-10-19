package com.glsx.plat.core.constant;

import com.glsx.plat.core.web.R;

/**
 * 系统返回码
 **/
public enum ResultConstants {

    ARGS_ERROR(601, "参数错误"),
    ARGS_NULL(602, "参数错误,必填参数 [%s]"),
    NOT_LOGIN(603, "未登录或登录失效"),
    NO_LOGIN_INVALID(604, "免登录失效"),

    SIGN_ERROR(701, "签名验证失败"),
    ILLEGAL_ACCESS(702, "非法访问"),
    ACCESS_DENIED(703, "禁止访问 %s"),
    PERMISSION_DENIED(704, "没有权限"),
    DATA_PERMISSION_DENIED(705, "无数据权限"),
    CHANNEL_NOT_SUPPORT(706, "非法访问渠道"),
    NOT_SUPPORT_OPERATOR(707, "不支持的操作"),

    REPEAT_DATA(801, "数据重复 %s"),
    DATA_TO_LARGE(802, "数据过大"),
    FILE_TO_LARGE(803, "文件过大"),
    NETWORK_ERROR(804, "网络连接错误或磁盘不可读"),
    NOT_SUPPORT_MIME(805, "不支持的 MIME类型,当前类型为:%s"),

    ;

    R r = new R();

    ResultConstants(int code, String message) {
        r.setCode(code);
        r.setMessage(message);
    }

    /**
     * 自定义消息的结果返回
     *
     * @param args
     * @return
     */
    public R result(Object... args) {
        String message = r.getMessage();
        r.setMessage(String.format(message, args));
        return r;
    }

    public int getCode() {
        return r.getCode();
    }

    public String getMsg() {
        return r.getMessage();
    }

}
