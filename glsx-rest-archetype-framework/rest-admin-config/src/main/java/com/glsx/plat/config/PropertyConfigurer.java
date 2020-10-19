package com.glsx.plat.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;

/**
 * 一、硬编码方式获取配置
 * 二、@RefreshScope+@Value
 */
@Component
public class PropertyConfigurer {

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    public ConfigurableEnvironment getEnvironment() {
        return applicationContext.getEnvironment();
    }

    public String getString(String key) {
        return getEnvironment().getProperty(key);
    }

    public String getProperty(String key) {
        return getEnvironment().getProperty(key);
    }

    public String getProperty(String key, String defaultVal) {
        return getEnvironment().getProperty(key, defaultVal);
    }

}
