package cn.com.glsx.echocenter.service;

import cn.com.glsx.echocenter.services.echoservice.EchoService;
import cn.com.glsx.echocenter.services.echoservice.req.EchoReq;
import cn.com.glsx.echocenter.services.echoservice.resp.EchoResp;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @author payu
 */
@Service
public class EchoServiceImpl implements EchoService {

    @Override
    public String echo(String msg) {
        return "Echo Center :" + msg;
    }

    @Override
    public String echo(String title, String msg) {
        return "Echo Center :" + title + ">>>" + msg;
    }

    @Override
    public EchoResp echo(EchoReq req) {
        EchoResp resp = new EchoResp();
        BeanUtils.copyProperties(req, resp);
        resp.setRemark("this is a echo test!");
        return resp;
    }

}
