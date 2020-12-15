package com.glsx.plat.im.rongcloud;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class RongCloudConfig {

    @Value("${rongcloud.appKey:}")
    private String appKey;

    @Value("${rongcloud.appSecret:}")
    private String appSecret;

}
