package com.glsx.plat.exception;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author payu
 */
@Setter
@Getter
public class ServiceException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 1L;

    private String msg;
    private String code;

    public static ServiceException create(String code, String msg) {
        ServiceException serviceException = new ServiceException();
        serviceException.setCode(code);
        serviceException.setMsg(msg);
        return serviceException;
    }

}
