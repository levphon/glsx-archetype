package com.glsx.plat.feign.interceptor;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class OkHttpTokenInterceptor implements Interceptor {

//    @Autowired
//    @Lazy
//    private UserAuthConfig userAuthConfig;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request newRequest = chain.request()
                .newBuilder()
//                .header(userAuthConfig.getTokenHeader(), BaseContextHandler.getToken())
                .build();

        Response response = chain.proceed(newRequest);
//        if (HttpStatus.FORBIDDEN.value() == response.code()) {
//            if (response.body().string().contains(String.valueOf(CommonConstants.EX_CLIENT_INVALID_CODE))) {
//                log.info("Client Token Expire,Retry to request...");
//                serviceAuthUtil.refreshClientToken();
//                newRequest = chain.request()
//                        .newBuilder()
//                        .header(userAuthConfig.getTokenHeader(), BaseContextHandler.getToken())
//                        .header(serviceAuthConfig.getTokenHeader(), serviceAuthUtil.getClientToken())
//                        .build();
//                response = chain.proceed(newRequest);
//            }
//        }
        return response;
    }

}
