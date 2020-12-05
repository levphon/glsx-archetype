package com.glsx.plat.loggin.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

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
@Document(collection = "sys_log")
public class SysLogEntity implements Serializable {

    @Id
    @ExcelIgnore
    private String id;

    /**
     * 应用/服务名
     */
    @ExcelIgnore
//    @ExcelProperty(value = "应用名称", index = 0)
    private String application;

    /**
     * 模块功能
     */
    @ExcelProperty(value = "系统菜单", index = 0)
    private String module;

    /**
     * 执行方法
     */
    @ExcelIgnore
    private String processed;

    /**
     * 请求参数
     */
    @ExcelIgnore
    private String requestData;

    /**
     * 操作类型
     */
    @ExcelProperty(value = "操作类型", index = 1)
    private String action;

    /**
     * 操作人
     */
    @ExcelIgnore
    private Long operator;

    /**
     * 操作人
     */
    @ExcelProperty(value = "操作账号", index = 2)
    private String operatorName;

    /**
     * 租户
     */
    @ExcelIgnore
    private String tenant;

    /**
     * 所属组织
     */
    @ExcelProperty(value = "所属组织", index = 3)
    private String belongOrg;

    /**
     * ip地址
     */
    @ExcelProperty(value = "主机IP", index = 4)
    private String ip;

    /**
     * 操作结果
     */
    @ExcelProperty(value = "操作结果", index = 5)
    private String result;

    /**
     * 时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ExcelProperty(value = "操作时间", index = 6)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdDate;

}
