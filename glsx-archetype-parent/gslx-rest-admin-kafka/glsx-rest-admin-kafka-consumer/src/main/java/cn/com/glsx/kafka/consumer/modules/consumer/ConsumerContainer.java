package cn.com.glsx.kafka.consumer.modules.consumer;

import cn.com.glsx.kafka.consumer.modules.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author payu
 */
@Slf4j
@Component
public class ConsumerContainer {

    @Autowired
    private TestService testService;

    @KafkaListener(id = "testConsumer",
            topics = "#{'${topic.test_topic}'.split(',')}",
            errorHandler = "consumerAwareErrorHandler")
    public void listenTest(ConsumerRecord<?, ?> record) {
        //errorHandler异常处理器注解里面使用的也是bean的名称
        //判断是否为null
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            //得到Optional实例中的值
            Object message = kafkaMessage.get();
            testService.print1(message.toString());
        }
    }

}
