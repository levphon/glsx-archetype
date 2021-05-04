package com.glsx.plat.kafka.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author fengzhi
 * @time 2020/12/4
 * @function kafka的nacos配置属性
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "kafka.config")
public class KafkaProperties {

    private String zkConnect;

    private String zkConnectTimeout;

    private String zkSessionTimeout;

    private String groupId;

    private String autoCommitInterval;

    private String autoOffsetReset;

    private String rebalanceBackoff;

    private String rebalanceMaxRetries;

}
