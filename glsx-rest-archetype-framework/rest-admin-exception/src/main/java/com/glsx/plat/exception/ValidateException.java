package com.glsx.plat.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * 〈一句话功能简述〉<br>
 * 验证类异常
 *
 * @author payu
 * @create 3/19/2019 16:40
 * @since 1.0.0
 */
@Setter
@Getter
public class ValidateException extends RuntimeException {

    private String msg;
    private String code = "600";

    public ValidateException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public ValidateException(String msg, String code) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }

    public ValidateException(String msg, Throwable e) {
        super(msg, e);
        this.msg = msg;
    }

    public ValidateException(String code, String msg, Throwable e) {
        super(msg, e);
        this.msg = msg;
        this.code = code;
    }

}
