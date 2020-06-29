package com.glsx.plat.jwt.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * @author payu
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "jwt.config.enabled", matchIfMissing = true)
public class JwtConfigAutoConfiguration {
}
