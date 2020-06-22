package com.glsx.microservice.fallback;

import com.glsx.microservice.api.EchoCenterFeignService;
import com.glsx.microservice.services.echoservice.req.EchoReq;
import com.glsx.microservice.services.echoservice.resp.EchoResp;
import org.springframework.stereotype.Component;

/**
 * Feign Fallback 处理
 *
 * @author payu
 */
@Component
public class EchoCenterFeignServiceFallback implements EchoCenterFeignService {

    @Override
    public String echo(String message) {
        return "服务未启动>>>" + message;
    }

    @Override
    public String echo2(String title, String message) {
        return "服务未启动>>>title:" + title + " message:" + message;
    }

    @Override
    public EchoResp echo3(EchoReq req) {
        return new EchoResp();
    }

}