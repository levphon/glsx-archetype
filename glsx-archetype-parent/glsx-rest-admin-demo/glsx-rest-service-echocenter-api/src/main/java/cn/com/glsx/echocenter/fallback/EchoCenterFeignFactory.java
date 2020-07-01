package cn.com.glsx.echocenter.fallback;

import cn.com.glsx.echocenter.api.EchoCenterFeignClient;
import feign.hystrix.FallbackFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EchoCenterFeignFactory implements FallbackFactory<EchoCenterFeignClient> {

    private final EchoCenterFeignClientFallback fallback;

    @Autowired
    public EchoCenterFeignFactory(EchoCenterFeignClientFallback fallback) {
        this.fallback = fallback;
    }

    @Override
    public EchoCenterFeignClient create(Throwable cause) {
        //打印下异常
        cause.printStackTrace();
        return fallback;
    }

}
