package com.glsx.microservice.services.echoservice.resp;

import lombok.Data;

@Data
public class EchoResp {

    private String title;
    private String message;
    private String user;
    private String remark;

}
