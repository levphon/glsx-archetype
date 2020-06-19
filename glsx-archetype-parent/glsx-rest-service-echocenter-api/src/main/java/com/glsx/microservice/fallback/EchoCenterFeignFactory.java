package com.glsx.microservice.fallback;

import com.glsx.microservice.api.EchoCenterFeignService;
import feign.hystrix.FallbackFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EchoCenterFeignFactory implements FallbackFactory<EchoCenterFeignService> {

    private final EchoCenterFeignServiceFallback fallback;

    @Autowired
    public EchoCenterFeignFactory(EchoCenterFeignServiceFallback fallback) {
        this.fallback = fallback;
    }

    @Override
    public EchoCenterFeignService create(Throwable cause) {
        //打印下异常
        cause.printStackTrace();
        return fallback;
    }

}
