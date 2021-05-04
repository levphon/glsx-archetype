package com.glsx.plat.kafka.config;

import com.glsx.plat.kafka.constants.KafkaConstant;
import kafka.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @author fengzhi
 * @time 2020/12/4
 * @function kafka的公共配置
 */
@Configuration
@EnableConfigurationProperties(KafkaProperties.class)
public class KafkaConfig {

    @Autowired
    private KafkaProperties kafkaProperties;

    @Bean
    public PropertiesFactoryBean consumerProperties() {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setProperties(getProperties());
        return propertiesFactoryBean;
    }

    @Bean
    public ConsumerConfig consumerConfig() {
        return new ConsumerConfig(getProperties());
    }

    /**
     * nacos属性封装Properties
     */
    public Properties getProperties() {
        Properties properties = new Properties();
        properties.setProperty(KafkaConstant.ZOOKEEPER_CONNECT, kafkaProperties.getZkConnect());
        properties.setProperty(KafkaConstant.ZOOKEEPER_CONNECT_TIMEOUT, kafkaProperties.getZkConnectTimeout());
        properties.setProperty(KafkaConstant.ZOOKEEPER_SESSION_TIMEOUT, kafkaProperties.getZkSessionTimeout());
        properties.setProperty(KafkaConstant.GROUP_ID, kafkaProperties.getGroupId());
        properties.setProperty(KafkaConstant.AUTO_COMMIT_INTERVAL, kafkaProperties.getAutoCommitInterval());
        properties.setProperty(KafkaConstant.AUTO_OFFSET_RESET, kafkaProperties.getAutoOffsetReset());
        properties.setProperty(KafkaConstant.REBLANCE_BACKOFF, kafkaProperties.getRebalanceBackoff());
        properties.setProperty(KafkaConstant.REBLANCE_MAX_RETRIES, kafkaProperties.getRebalanceMaxRetries());
        return properties;
    }
}
