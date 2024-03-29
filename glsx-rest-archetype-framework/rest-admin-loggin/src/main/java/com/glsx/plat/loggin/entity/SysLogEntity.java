package com.glsx.plat.loggin.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
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
@Table(name = "t_sys_log")
@Document(collection = "sys_log")
public class SysLogEntity implements Serializable {

    @Id
    @ExcelIgnore
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "SELECT LAST_INSERT_ID()")
    private String id;

    /**
     * 应用/服务名
     */
    @ExcelIgnore
//    @ExcelProperty(value = "应用名称")
    private String application;

    /**
     * 模块功能
     */
    @ExcelProperty(value = "模块功能")
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
    @Column(name = "request_data")
    private String requestData;

    @ExcelProperty(value = "操作类型")
    private String action;

    @ExcelProperty(value = "操作说明")
    private String remark;

    /**
     * 租户
     */
    @ExcelIgnore
    @Transient
    private String tenant;

    @ExcelIgnore
//    @ExcelProperty(value = "所属组织")
    @Column(name = "belong_org")
    private String belongOrg;

    @ExcelProperty(value = "请求IP")
    private String ip;

    @ExcelProperty(value = "操作结果")
    private String result;

    @ExcelIgnore
    private String platform;

    /**
     * 操作人
     */
    @ExcelIgnore
    @Column(name = "created_by")
    private Long createdBy;

    @ExcelProperty(value = "操作账号")
    @Column(name = "created_name")
    private String createdName;

    @Transient
    @ExcelProperty(value = "账号名称")
    private String username;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ExcelProperty(value = "操作时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_date", updatable = false)
    private Date createdDate;

}
