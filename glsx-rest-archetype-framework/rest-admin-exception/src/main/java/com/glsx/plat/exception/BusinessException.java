package com.glsx.plat.exception;

import com.glsx.plat.core.web.R;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;

/**
 * 系统业务异常,异常号段为 :
 * 200 : 成功
 * 400 以下 {@link HttpStatus status code}占用码
 * 600 ~ 9999 内定系统异常段
 * 10000 ~ 99999 自定义异常码段
 * 100000 ~ Integer.MAX_VALUE 动态异常码段
 *
 * @author payu
 */
public class BusinessException extends RuntimeException {

    protected R r;

    protected static final int MIN_AUTO_CODE = 100000;

    protected BusinessException(BusinessException parent) {
        if (parent != null) {
            this.r = parent.r;
        }
    }

    /**
     * 内部系统用，对外api接口不能使用随机错误码
     *
     * @param message
     * @return
     */
    public static BusinessException create(String message) {
        int value = (int) (MIN_AUTO_CODE + Math.round((Integer.MAX_VALUE - MIN_AUTO_CODE) * Math.random()));
        return create(value, message);
    }

    /**
     * 返回码为整型
     *
     * @param returnCode
     * @param message
     * @return
     */
    public static BusinessException create(int returnCode, String message) {
        BusinessException businessException = new BusinessException(null);
        businessException.r = R.error(returnCode, message);
        return businessException;
    }

    /**
     * 返回码可以为字符串型
     *
     * @param returnCode
     * @param message
     * @return
     */
    public static BusinessException create(String returnCode, String message) {
        BusinessException businessException = new BusinessException(null);
        businessException.r = R.error(returnCode, message);
        return businessException;
    }

    public static BusinessException create(R r) {
        BusinessException businessException = new BusinessException(null);
        businessException.r = r;
        return businessException;
    }

    public static BusinessException create(ExceptionCause exceptionCause, Object... args) {
        R r = exceptionCause.result();
        String message = r.getMessage();

        if (ArrayUtils.isNotEmpty(args)) {
            String[] argsStringArray = new String[args.length];
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                argsStringArray[i] = ObjectUtils.toString(arg);
            }
            String formatMessage = String.format(message, argsStringArray);
            r.setMessage(formatMessage);
        }

        BusinessException businessException = new BusinessException(null);
        businessException.r = r;
        return businessException;
    }

    @Override
    public String getMessage() {
        return r.getMessage();
    }

    public R getResultEntity() {
        return r;
    }

}
