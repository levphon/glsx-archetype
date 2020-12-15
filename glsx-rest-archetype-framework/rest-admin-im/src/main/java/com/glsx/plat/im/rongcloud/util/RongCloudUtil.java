package com.glsx.plat.im.rongcloud.util;

import com.glsx.plat.im.rongcloud.RongCloudConfig;
import io.rong.RongCloud;
import io.rong.models.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RongCloudUtil implements InitializingBean {

    private RongCloud rongCloud;

    @Autowired
    private RongCloudConfig config;

    @Override
    public void afterPropertiesSet() {
        rongCloud = RongCloud.getInstance(config.getAppKey(), config.getAppSecret());
    }

    public RongCloud getRongCloud() {
        return rongCloud;
    }

    public boolean isSuccess(Result result) {
        if (result == null || result.getCode() == null) {
            return false;
        }
        return result.getCode() == 200;
    }

}
