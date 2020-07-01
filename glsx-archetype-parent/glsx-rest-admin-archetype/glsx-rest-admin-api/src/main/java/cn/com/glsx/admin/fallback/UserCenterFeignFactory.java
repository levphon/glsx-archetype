package cn.com.glsx.admin.fallback;

import cn.com.glsx.admin.api.UserCenterFeignService;
import feign.hystrix.FallbackFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserCenterFeignFactory implements FallbackFactory<UserCenterFeignService> {

    private final UserCenterFeignServiceFallback fallback;

    @Autowired
    public UserCenterFeignFactory(UserCenterFeignServiceFallback fallback) {
        this.fallback = fallback;
    }

    @Override
    public UserCenterFeignService create(Throwable cause) {
        //打印异常
        cause.printStackTrace();
        return fallback;
    }

}
