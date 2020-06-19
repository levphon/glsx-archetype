package com.glsx.plat.exception;

import lombok.Data;

@Data
public class ExceptionInfo {

    private Long timestamp;

    private Integer status;
    //异常包结构-"com.crazy.cloud.common.exception.DataConflictException"
    private String exception;
    //异常信息
    private String message;

    private String path;

    private String error;

}
