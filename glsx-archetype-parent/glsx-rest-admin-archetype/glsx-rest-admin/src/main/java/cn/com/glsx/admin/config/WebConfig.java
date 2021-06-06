package cn.com.glsx.admin.config;

import cn.com.glsx.admin.modules.utils.JwtUser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.glsx.plat.common.utils.DateUtils;
import com.glsx.plat.context.properties.UploadProperties;
import com.glsx.plat.web.interceptor.LogInterceptor;
import com.glsx.plat.web.interceptor.VisitInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author liuyf
 * @Title WebConfig.java
 * @Description 系统的配置相关文件，如拦截器，过滤器，跨域等
 * @date 2019年7月18日 下午4:45:06
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private UploadProperties uploadProperties;

    /**
     * 基于MDC的简单的日志链路
     */
    @Resource
    private LogInterceptor logInterceptor;

    /**
     * 基于jwt的token认证
     */
    @Resource
    private VisitInterceptor<JwtUser> visitInterceptor;

    /**
     * 静态资源
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/META-INF/resources/")
                .addResourceLocations("classpath:/resources/")
                .addResourceLocations("classpath:/static/")
                .addResourceLocations("classpath:/public/")
                .addResourceLocations("/dist/").addResourceLocations();

        String path = uploadProperties.getBasePath();
        registry.addResourceHandler("/files/**").addResourceLocations("file://" + path);
    }

    /**
     * 使用此方法, 以下 spring-boot: jackson时间格式化 配置 将会失效
     * spring.jackson.time-zone=GMT+8
     * spring.jackson.date-format=yyyy-MM-dd HH:mm:ss
     * 原因: 会覆盖 @EnableAutoConfiguration 关于 WebMvcAutoConfiguration 的配置
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = converter.getObjectMapper();
        // 生成JSON时,将所有Long转换成String
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        objectMapper.registerModule(simpleModule);
        // 时间格式化
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setDateFormat(new SimpleDateFormat(DateUtils.NORMAL_DATE_TIME_PATTERN));
        // 设置格式化内容
        converter.setObjectMapper(objectMapper);
        converters.add(0, converter);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //1.加入的顺序就是拦截器执行的顺序，
        //2.按顺序执行所有拦截器的preHandle
        //3.所有的preHandle 执行完再执行全部postHandle 最后是postHandle
        registry.addInterceptor(visitInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/")
                .excludePathPatterns("/login")
                .excludePathPatterns("/actuator/**")
                .excludePathPatterns("/api/**")
                .excludePathPatterns(uploadProperties.getFilepath());
        registry.addInterceptor(logInterceptor).addPathPatterns("/**");
    }

    /**
     * 有问题，建议用filter
     * https://blog.csdn.net/weixin_33958585/article/details/88678712
     *
     * @param registry
     */
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOrigins(origins.split(","))
//                .allowedHeaders("Authorization, Content-Type, X-Requested-With")
//                .allowedMethods("POST, PUT, GET, OPTIONS, DELETE")
//                .allowCredentials(true)
//                .maxAge(3600);
//    }

}
