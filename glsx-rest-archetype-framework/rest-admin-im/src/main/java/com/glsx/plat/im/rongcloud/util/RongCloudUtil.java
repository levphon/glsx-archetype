package com.glsx.plat.im.rongcloud.util;

import io.rong.RongCloud;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RongCloudUtil implements InitializingBean {

    @Value("${rongcloud.appKey:}")
    private String appKey;

    @Value("${rongcloud.appSecret:}")
    private String appSecret;

    private RongCloud rongCloud;

    @Override
    public void afterPropertiesSet() {
        rongCloud = RongCloud.getInstance(appKey, appSecret);
    }


    public RongCloud getRongCloud() {
        return rongCloud;
    }

}
