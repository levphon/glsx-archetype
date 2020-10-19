package cn.com.glsx;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author payu
 */
@Slf4j
@EnableFeignClients(basePackages = "cn.com.glsx.*.api")
@EnableDiscoveryClient
@SpringBootApplication
public class BusinessServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BusinessServiceApplication.class, args);
        log.info("业务服务启动成功.");
    }

}