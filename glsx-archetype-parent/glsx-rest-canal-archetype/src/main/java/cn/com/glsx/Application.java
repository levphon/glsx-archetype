package cn.com.glsx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.jmx.support.RegistrationPolicy;
import tk.mybatis.spring.annotation.MapperScan;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

import static java.time.ZoneId.of;
import static java.util.TimeZone.getTimeZone;

/**
 * @author zhouhaibao
 */
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
@SpringBootApplication(scanBasePackages = {"cn.com.glsx"})
@MapperScan("cn.com.glsx.canal.modules.mapper")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @PostConstruct
    void started() {
        TimeZone.setDefault(getTimeZone(of("Asia/Shanghai")));
    }

}
