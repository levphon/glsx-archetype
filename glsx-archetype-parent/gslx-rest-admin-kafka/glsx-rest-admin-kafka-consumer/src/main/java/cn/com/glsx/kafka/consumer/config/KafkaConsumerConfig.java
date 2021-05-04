package cn.com.glsx.kafka.consumer.config;

import cn.com.glsx.kafka.consumer.modules.consumer.TestConsumer;
import kafka.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author fengzhi
 * @time 2020/12/9
 * @function kafka的消费者配置
 */
@Configuration
public class KafkaConsumerConfig {

    @Autowired
    private ConsumerConfig consumerConfig;

    @Value("${topic.test_topic.nThreads}")
    private Integer nThreads;

    @Bean(initMethod = "init", destroyMethod = "destroy")
    public TestConsumer testConsumer(@Value("${topic.test_topic}") String topic) {
        TestConsumer testConsumer = new TestConsumer();
        testConsumer.setnThreads(nThreads);
        testConsumer.setConsumerConfig(consumerConfig);
        testConsumer.setTopic(topic);
        return testConsumer;
    }

}
