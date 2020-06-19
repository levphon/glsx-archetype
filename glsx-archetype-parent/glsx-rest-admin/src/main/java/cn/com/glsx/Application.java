package cn.com.glsx;

import cn.com.glsx.admin.config.ImportConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.context.annotation.Import;
import org.springframework.jmx.support.RegistrationPolicy;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.spring.annotation.MapperScan;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

import static java.time.ZoneId.of;
import static java.util.TimeZone.getTimeZone;

/**
 * @author payu
 */
@Import({ImportConfig.class})
@MapperScan("com.glsx.vasp.modules.mapper")//如果glsx-rest-system目录结构和glsx-rest-admin一样，这个注解可以省略
@EnableDiscoveryClient
@SpringBootApplication
@EnableTransactionManagement
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @PostConstruct
    void started() {
        TimeZone.setDefault(getTimeZone(of("Asia/Shanghai")));
    }

}
