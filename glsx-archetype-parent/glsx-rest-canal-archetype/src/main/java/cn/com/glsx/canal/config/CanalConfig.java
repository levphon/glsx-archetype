package cn.com.glsx.canal.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhouhaibao
 * @version 1.0
 * @description: canal配置
 * @date 2021/6/22 14:17
 */
@Configuration
@ConfigurationProperties("canal")
@Data
public class CanalConfig {

    private String ip;

    private int port;

    private String destination;

}
