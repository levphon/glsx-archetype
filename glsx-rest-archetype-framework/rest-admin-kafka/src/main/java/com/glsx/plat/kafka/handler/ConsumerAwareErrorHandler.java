package com.glsx.plat.kafka.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.springframework.kafka.listener.ConsumerAwareListenerErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * 消费者异常处理器(和rabbitMQ中的一样)
 */
@Slf4j
@Component
public class ConsumerAwareErrorHandler implements ConsumerAwareListenerErrorHandler {

    @Override
    public Object handleError(Message<?> message, ListenerExecutionFailedException exception, Consumer<?, ?> consumer) {

        //异常处理类,此类可以做重试策略，当消费者出现异常的时候发送给其它topic下的分区
        message.getHeaders();//可以得到你想要信息

        log.error(message.getPayload().toString());
        return null;
    }

}
