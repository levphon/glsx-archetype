package com.glsx.plat.context.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 1、mysql
 * 2、mongodb
 * 3、log文件 + ELK
 * 4、mq + ELK
 */
@Data
@Entity
@Table(name = "t_log")
public class SysLogEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    /**
     * 模块功能
     */
    private String modul;

    /**
     * 操作人
     */
    @Column(name = "operator", length = 10)
    private Long operator;

    /**
     * 请求参数
     */
    @Column(name = "request_data", length = 512)
    private String requestData;

    /**
     * ip地址
     */
    @Column(name = "ip", length = 128)
    private String ip;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "created_date")
    private Date createDate = new Date();

}
