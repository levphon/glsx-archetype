package cn.com.glsx.kafka.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author fengzhi
 * @version 1.0.0
 * @ClassName KafkaProductorConfig.java
 * @createTime 2020年03月12日 16:21:00
 */
@Data
@Configuration
@ConfigurationProperties("mq.kafka")
public class MqConfig {

    /**
     * kafka 生产者服务端地址
     */
    private String productorServer;

    /**
     * 符合场景时间规则的数据发送 topic
     */
    private String sceneTopic;

    /**
     * 预加载的时间范围
     */
    private String timeFuture;

}
