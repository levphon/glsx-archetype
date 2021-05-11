
package cn.com.glsx.consumer.comsumers;

import cn.com.glsx.consumer.common.AESBase64Utils;
import cn.com.glsx.consumer.common.Contants;
import cn.com.glsx.consumer.common.MqConsumer;
import cn.com.glsx.consumer.entity.MessageVO;
import cn.com.glsx.consumer.service.TuyaConmsumerService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

//@Component
@Slf4j
public class TuyaComsumer implements CommandLineRunner {

    @Value("${pulsar.serviceUrl}")
    private String serviceUrl;

    @Value("${pulsar.accessId}")
    private String accessId;

    @Value("${pulsar.accessKey}")
    private String accessKey;

    @Value("${pulsar.maxRedeliverCount}")
    private int maxRedeliverCount;

    @Value("${pulsar.topic}")
    private String topic;

    @Autowired
    private TuyaConmsumerService tuyaConmsumerService;

    @Override
    public void run(String... args) throws Exception {
        MqConsumer mqConsumer = MqConsumer.build().serviceUrl(serviceUrl).accessId(accessId).accessKey(accessKey).topic(topic)
                .maxRedeliverCount(maxRedeliverCount).messageListener(
                        message -> {
                            log.info("Message received:" + new String(message.getData()) + ",seq="
                                    + message.getSequenceId() + ",time=" + message.getPublishTime() + ",consumed time="
                                    + System.currentTimeMillis());
                            String jsonMessage = new String(message.getData());
                            MessageVO vo = JSON.parseObject(jsonMessage, MessageVO.class);
                            Integer protocol = vo.getProtocol();
                            if(protocol.equals(Contants.STATUS_APPEAR)){
                                String data = AESBase64Utils.decrypt(vo.getData(), accessKey.substring(8, 24));
                                log.info("the real message data:" + data);
                                tuyaConmsumerService.processMessage(data);
                            }
                        }
                );
        mqConsumer.start();
    }
}
