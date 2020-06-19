package cn.com.glsx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.jmx.support.RegistrationPolicy;

/**
 * @author payu
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}