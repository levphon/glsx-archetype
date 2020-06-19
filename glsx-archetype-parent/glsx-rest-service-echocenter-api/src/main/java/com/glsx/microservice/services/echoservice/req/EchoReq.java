package com.glsx.microservice.services.echoservice.req;

import lombok.Data;

@Data
public class EchoReq {

    private String title;
    private String message;
    private String user;
    private String sign;

}
