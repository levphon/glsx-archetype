package cn.com.glsx.echocenter.api;

import cn.com.glsx.echocenter.services.echoservice.req.EchoReq;
import cn.com.glsx.echocenter.services.echoservice.resp.EchoResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * echo中心
 * fallback 两种模式：
 * 1、EchoCenterFeignServiceFallback.class 获取不到HTTP请求错误状态码和信息
 * 2、EchoCenterFeignFactory.class 工厂模式
 *
 * @author payu
 */
@FeignClient(name = "glsx-rest-echo-center", contextId = "hello", path = "hello", decode404 = true)
public interface HelloCenterFeignClient {

    @GetMapping(value = "/{message}")
    String echo(@PathVariable("message") String message);

    @GetMapping("/echo2")
    String echo2(@RequestParam("title") String title, @RequestParam("message") String message);

    @PostMapping("/echo3")
    EchoResp echo3(@RequestBody EchoReq req);

}
