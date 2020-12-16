package com.glsx.plat.push.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author payu
 */
@Data
@Component
@ConfigurationProperties(prefix = "getui")
public class GetuiProperties {

    String appId;
    String appKey;
    String appSecret;
    String masterSecret;

}
