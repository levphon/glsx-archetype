package cn.com.glsx.kafka.consumer.modules.consumer;

import cn.com.glsx.kafka.consumer.modules.service.TestService;
import com.glsx.plat.kafka.base.MessageReceiver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author payu
 */
@Slf4j
public class TestConsumer extends MessageReceiver {

    @Autowired
    private TestService testService;

    @Override
    public void processMessage(String topic, byte[] message) {
        String msg = new String(message);
        testService.print2(msg);
    }

}
