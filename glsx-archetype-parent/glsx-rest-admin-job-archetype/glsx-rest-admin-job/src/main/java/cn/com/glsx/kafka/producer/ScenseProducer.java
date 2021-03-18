package cn.com.glsx.kafka.producer;

import cn.com.glsx.kafka.config.MqConfig;
import cn.com.glsx.modules.model.param.SceneReq;
import com.alibaba.fastjson.JSON;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

@Component
@Slf4j
public class ScenseProducer {
    @Resource
    private Producer productor;

    @Autowired
    private MqConfig mqConfig;

    /**
     * 公共的消息发送
     *
     * @return
     */
    public void sendKafkaMessage(SceneReq scense) {
        try {
            String topic = mqConfig.getSceneTopic();
            String sendMessage = JSON.toJSONString(scense);//关键;
            KeyedMessage<byte[], byte[]> message = new KeyedMessage<>(topic, String.valueOf(scense.getScenseId()).getBytes(), sendMessage.getBytes());
            productor.send(message);
            log.info("sendKafkaMessageSceneReq success topic:" + topic + "  success:" + scense.getScenseId()+"  type:"+scense.getType());
        } catch (Exception e) {
            log.error("sendKafkaMessageSceneReq failed scenseId:" + scense.getScenseId() + "==msg:" + e.getMessage());
        }
    }
}
