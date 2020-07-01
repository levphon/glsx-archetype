package cn.com.glsx.echocenter.fallback;

import cn.com.glsx.echocenter.api.EchoCenterFeignService;
import cn.com.glsx.echocenter.services.echoservice.req.EchoReq;
import cn.com.glsx.echocenter.services.echoservice.resp.EchoResp;
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