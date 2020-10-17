package com.glsx.plat.kafka.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author payu
 */
@Component
public class LogProducer {

    @Resource
    private KafkaTemplate kafkaTemplate;

    public void sendLog(String sysLog) {
        kafkaTemplate.send("test_topic", sysLog);
    }

}
