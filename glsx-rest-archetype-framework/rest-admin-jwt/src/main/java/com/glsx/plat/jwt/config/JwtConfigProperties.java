package com.glsx.plat.jwt.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author payu
 */
@Data
@Component
@ConfigurationProperties("jwt.config")
public class JwtConfigProperties {

    /**
     * token请求头key
     */
    private String header;

    /**
     * 签名的失效时间
     */
    private Long ttl;

    /**
     * 签名私钥
     */
    private String key;

    /**
     * token黑名单前缀
     */
    private String blacklistKey;

}
