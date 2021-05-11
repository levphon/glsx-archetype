package cn.com.glsx.consumer.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class MessageVO implements Serializable {


    private String data;
    private Integer protocol;
    private String pv;
    private String sign;
    private Long t;

}