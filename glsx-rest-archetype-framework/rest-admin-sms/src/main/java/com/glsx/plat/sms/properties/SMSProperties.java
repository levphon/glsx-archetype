package com.glsx.plat.sms.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "sms")
public class SMSProperties {

    String url;
    String source;

    String accessKeyId;
    String accessKeySecret;
    String signName;
    String template;

}
