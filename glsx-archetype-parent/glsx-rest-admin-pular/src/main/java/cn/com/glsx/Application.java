package cn.com.glsx;

import com.glsx.plat.context.EnableRestAdmin;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.jmx.support.RegistrationPolicy;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author Pig/linxiaofa
 * @date 2020/8/5 4:22 下午
 */
@EnableRestAdmin
@EnableDiscoveryClient
@EnableTransactionManagement
@MapperScan(basePackages = "cn.com.glsx.consumer.mapper")
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
@SpringBootApplication(scanBasePackages = {"cn.com.glsx"})
public class Application {

    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
