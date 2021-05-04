package cn.com.glsx.kafka.test;

import cn.com.glsx.Application;
import cn.com.glsx.kafka.producer.modules.producer.TestProducer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProducerApplicationTests {

    @Autowired
    private TestProducer testProducer;

    @Test
    public void testSendMsg() {
        for (int i = 0; i <= 100; i++) {
            testProducer.sendMsg("this is a test " + i);
        }
    }

}