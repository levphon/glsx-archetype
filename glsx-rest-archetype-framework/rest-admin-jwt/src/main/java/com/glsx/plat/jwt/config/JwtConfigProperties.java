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
     * 签名的有效时间
     */
    private Long ttl;

    /**
     * 允许刷新签名的有效时间
     * 定义允许刷新JWT的有效时长(在这个时间范围内，用户的JWT过期了，不需要重新登录，后台会给一个新的JWT给前端，这个叫Token的刷新机制后面会着重介绍它的意义。)
     */
    private Long refreshTtl;

    /**
     * 签名私钥
     */
    private String key;

    /**
     * token黑名单前缀
     */
    private String blacklistKey;

}
