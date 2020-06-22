package com.glsx.microservice.services.echoservice;

import com.glsx.microservice.services.echoservice.req.EchoReq;
import com.glsx.microservice.services.echoservice.resp.EchoResp;

public interface EchoService {

    public String echo(String msg);

    public String echo(String title, String msg);

    public EchoResp echo(EchoReq msg);

}
