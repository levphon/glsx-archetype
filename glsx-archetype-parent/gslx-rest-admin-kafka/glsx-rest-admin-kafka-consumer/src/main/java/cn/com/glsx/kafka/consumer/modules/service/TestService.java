package cn.com.glsx.kafka.consumer.modules.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TestService {

    public void print1(String message) {
        log.info(message);
    }

    public void print2(String message) {
        log.info(message);
    }

}
