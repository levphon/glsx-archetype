package com.glsx.plat.exception;

import com.glsx.plat.core.web.R;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 系统返回码
 *
 * @author payu
 * {@link HttpStatus status code}
 */
@Getter
public enum SystemMessage implements ExceptionCause<BusinessException> {

    SUCCESS(HttpStatus.OK.value(), "成功"),

    ARGS_ERROR(601, "参数错误"),
    ARGS_NULL(602, "参数错误,必填参数 [%s]"),
    NOT_LOGIN(603, "未登录或登录失效"),

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

    SERVICE_CALL_FAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "后台服务内部错误"),
    MICROSERVICE_CALL_FAIL(HttpStatus.SERVICE_UNAVAILABLE.value(), "服务调用异常");

    R r = new R();

    SystemMessage(int code, String message) {
        r.setCode(code);
        r.setMessage(message);
    }

    @Override
    public BusinessException exception(Object... args) {
        return BusinessException.create(this, args);
    }

    @Override
    public R result() {
        return r;
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
