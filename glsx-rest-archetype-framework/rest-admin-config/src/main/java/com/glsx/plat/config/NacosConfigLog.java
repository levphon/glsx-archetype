package com.glsx.plat.config;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Properties;

@Slf4j
@Component
public class NacosConfigLog {

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @PostConstruct
    public void init() throws NacosException {
        Environment env = applicationContext.getEnvironment();
        String serverAddr = env.getRequiredProperty("spring.cloud.nacos.config.server-addr");
        String group = env.getRequiredProperty("spring.cloud.nacos.config.group");

        String prefix = env.getProperty("spring.cloud.nacos.config.prefix");
        if (StringUtils.isEmpty(prefix)) {
            prefix = env.getRequiredProperty("spring.application.name");
        }
        String active = env.getProperty("spring.profiles.active", "dev");
        String extension = env.getProperty("spring.cloud.nacos.config.file-extension", "properties");
        String dataId = prefix + "-" + active + "." + extension;

        Properties properties = new Properties();
        properties.put(PropertyKeyConst.SERVER_ADDR, serverAddr);
        ConfigService configService = NacosFactory.createConfigService(properties);
        String content = configService.getConfig(dataId, group, 5000);
        log.info("- dataId {} config info\n{}", dataId, content);
    }

}
