package cn.com.glsx.echocenter.fallback;

import cn.com.glsx.echocenter.api.EchoCenterFeignService;
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
