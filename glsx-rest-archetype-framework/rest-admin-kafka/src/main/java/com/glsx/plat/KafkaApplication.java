//package com.glsx.plat;
//
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//
//import javax.annotation.PostConstruct;
//import java.util.TimeZone;
//
//import static java.time.ZoneId.of;
//import static java.util.TimeZone.getTimeZone;
//
///**
// * @author payu
// */
//@SpringBootApplication
//public class KafkaApplication {
//
//    public static void main(String[] args) {
//        SpringApplication.run(KafkaApplication.class, args);
//    }
//
//    @PostConstruct
//    void started() {
//        TimeZone.setDefault(getTimeZone(of("Asia/Shanghai")));
//    }
//
//}
