//package com.glsx.plat.kafka;
//
//
//import com.glsx.plat.KafkaApplication;
//import com.glsx.plat.core.entity.SysLogEntity;
//import com.glsx.plat.kafka.producer.LogProducer;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.util.Date;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = KafkaApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//public class DemoApplicationTests {
//
//    @Autowired
//    private LogProducer producer;
//
//    @Test
//    public void testProduceLog() {
//        SysLogEntity sysLog = new SysLogEntity();
//        sysLog.setModul("aaa");
//        sysLog.setIp("192.168.0.69");
//        sysLog.setCreateDate(new Date());
//        producer.sendLog(sysLog);
//    }
//
//}