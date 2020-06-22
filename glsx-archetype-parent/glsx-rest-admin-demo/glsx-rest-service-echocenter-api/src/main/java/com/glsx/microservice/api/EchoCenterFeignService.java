package com.glsx.microservice.api;

import com.glsx.microservice.fallback.EchoCenterFeignFactory;
import com.glsx.microservice.services.echoservice.req.EchoReq;
import com.glsx.microservice.services.echoservice.resp.EchoResp;
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
@FeignClient(name = "glsx-rest-echo-center", fallback = EchoCenterFeignFactory.class)
public interface EchoCenterFeignService {

    @GetMapping(value = "/echo/{message}")
    String echo(@PathVariable("message") String message);

    @GetMapping("/echo2")
    public String echo2(@RequestParam("title") String title, @RequestParam("message") String message);

    @PostMapping("/echo3")
    public EchoResp echo3(@RequestBody EchoReq req);

}
