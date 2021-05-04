package cn.com.glsx.kafka.producer.modules.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @author payu
 */
@Component
public class TestProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    public void sendMsg(String message) {
        kafkaTemplate.send("test_topic", message);
    }

}
