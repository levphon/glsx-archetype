package cn.com.glsx.admin.fallback;

import cn.com.glsx.admin.api.UserCenterFeignClient;
import feign.hystrix.FallbackFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserCenterFeignFactory implements FallbackFactory<UserCenterFeignClient> {

    private final UserCenterFeignClientFallback fallback;

    @Autowired
    public UserCenterFeignFactory(UserCenterFeignClientFallback fallback) {
        this.fallback = fallback;
    }

    @Override
    public UserCenterFeignClient create(Throwable cause) {
        //打印异常
        cause.printStackTrace();
        return fallback;
    }

}
