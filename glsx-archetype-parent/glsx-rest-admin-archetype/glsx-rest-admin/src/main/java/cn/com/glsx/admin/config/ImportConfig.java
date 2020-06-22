package cn.com.glsx.admin.config;

import com.glsx.plat.context.RestAdminAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 引入配置组件
 *
 * @author payu
 */
@Import({
        RestAdminAutoConfiguration.class,
//        FeignConfig.class
})
@Configuration
public class ImportConfig {
}
