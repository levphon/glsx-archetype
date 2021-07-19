package com.glsx.plat.im.rongcloud.exception;

import lombok.Data;

@Data
public class CusException extends Throwable {

    private String code;
    private String message;

    public CusException(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
