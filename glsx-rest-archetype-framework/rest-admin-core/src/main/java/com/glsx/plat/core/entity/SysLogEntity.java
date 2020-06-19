package com.glsx.plat.core.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 1、mysql
 * 2、mongodb
 * 3、log文件 + ELK
 * 4、mq + ELK
 *
 * @author payu
 */
@Data
public class SysLogEntity implements Serializable {

    /**
     * 模块功能
     */
    private String modul;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 请求参数
     */
    private String requestData;

    /**
     * ip地址
     */
    private String ip;

    /**
     * 时间
     */
    private Date createDate;

}
