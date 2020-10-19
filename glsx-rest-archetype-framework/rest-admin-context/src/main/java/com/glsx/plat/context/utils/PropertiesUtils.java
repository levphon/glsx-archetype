package com.glsx.plat.context.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 读取配置文件属性值
 *
 */
@Component
public class PropertiesUtils {

    private static Environment env;

    @Autowired
    public void setEnv(Environment env) {
        PropertiesUtils.env = env;
    }

    public static String getProperty(String key) {
        return env.getProperty(key);
    }

    public static String getProperty(String key, String defaultVal) {
        return env.getProperty(key, defaultVal);
    }

}
