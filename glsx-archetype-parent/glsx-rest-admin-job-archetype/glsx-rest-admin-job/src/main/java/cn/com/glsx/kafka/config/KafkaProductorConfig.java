package cn.com.glsx.kafka.config;

import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @author fengzhi
 * @version 1.0.0
 * @ClassName KafkaProductorConfig.java
 * @description: kafka生产消息
 * @createTime 2021年03月12日 16:21:00
 */
@Slf4j
@Configuration
public class KafkaProductorConfig {

    @Autowired
    private MqConfig mqConfig;

    @Bean("productor")
    public Producer getProducer() {
        ProducerConfig producerConfig = new ProducerConfig(initProperties());
        return new Producer(producerConfig);
    }

    private Properties initProperties() {
        Properties properties = new Properties();
        properties.setProperty("serializer.class", "kafka.serializer.DefaultEncoder");
        properties.setProperty("key.serializer.class", "kafka.serializer.DefaultEncoder");
        properties.setProperty("metadata.broker.list", mqConfig.getProductorServer());
        properties.setProperty("producer.type", "async");
        log.info("fz===kafka_server"+mqConfig.getProductorServer());
        return properties;
    }

}
