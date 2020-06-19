package com.glsx.plat.kafka.producer;

import com.glsx.plat.core.entity.SysLogEntity;
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

    public void sendLog(SysLogEntity sysLog) {
        kafkaTemplate.send("test_kafka", sysLog.toString());
    }

}
