package cn.com.glsx.echocenter.services.echoservice;

import cn.com.glsx.echocenter.services.echoservice.req.EchoReq;
import cn.com.glsx.echocenter.services.echoservice.resp.EchoResp;

public interface EchoService {

    public String echo(String msg);

    public String echo(String title, String msg);

    public EchoResp echo(EchoReq msg);

}
