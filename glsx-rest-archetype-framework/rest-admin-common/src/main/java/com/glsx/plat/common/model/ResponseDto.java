package com.glsx.plat.common.model;

import lombok.Data;

import java.util.List;

/**
 * @author zhouhaibao
 * @date 2021/5/10 10:28
 */
@Data
public class ResponseDto<T> {
    private int code;
    private String message;
    private String data;
    private T t;
    private List<T> tList;
    private PageDto<T> pageDto;
}