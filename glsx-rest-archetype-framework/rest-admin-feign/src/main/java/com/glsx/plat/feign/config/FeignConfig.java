package com.glsx.plat.feign.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import feign.Logger;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import feign.form.spring.converter.SpringManyMultipartFilesReader;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author payu
 */
@Configuration
@Import({FeignAutoConfiguration.class, OkHttpConfig.class})
public class FeignConfig {

    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;

    //Feign表单提交配置，还有解决中文乱码问题
    @Bean
    @Primary
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public SpringFormEncoder feignFormEncoder() {
        return new SpringFormEncoder(new SpringEncoder(messageConverters));
    }

    //feign拦截器，设置header信息
    @Bean
    FeignInterceptor feignInterceptor() {
        return new FeignInterceptor();
    }

    //开启feign日志
    @Bean
    Logger.Level feignLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public Encoder feignEncoder() {
        return new SpringEncoder(feignHttpMessageConverter());
    }

    @Bean
    public Decoder feignDecoder() {
        return new SpringDecoder(feignHttpMessageConverter());
    }

    private ObjectFactory<HttpMessageConverters> feignHttpMessageConverter() {
        final HttpMessageConverters httpMessageConverters = getSpringConverter();
//        final HttpMessageConverters httpMessageConverters = getGsonConverter();
//        final HttpMessageConverters httpMessageConverters = getFastJsonConverter();
        return () -> httpMessageConverters;
    }

    private HttpMessageConverters getSpringConverter() {
        List<HttpMessageConverter<?>> springConverters = messageConverters.getObject().getConverters();
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>(springConverters.size() + 1);
        messageConverters.addAll(springConverters);
        messageConverters.add(new SpringManyMultipartFilesReader(4096));
        return new HttpMessageConverters(true, messageConverters);
    }

    public HttpMessageConverters getGsonConverter() {
        GsonHttpMessageConverter converter = new GsonHttpMessageConverter();
        Collection<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        messageConverters.add(converter);
        return new HttpMessageConverters(true, messageConverters);
    }

    private HttpMessageConverters getFastJsonConverter() {
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        List<MediaType> supportedMediaTypes = new ArrayList<>();
        supportedMediaTypes.add(MediaType.APPLICATION_JSON);
        supportedMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        supportedMediaTypes.add(MediaType.APPLICATION_ATOM_XML);
        supportedMediaTypes.add(MediaType.APPLICATION_FORM_URLENCODED);
        supportedMediaTypes.add(MediaType.APPLICATION_OCTET_STREAM);
        supportedMediaTypes.add(MediaType.APPLICATION_PDF);
        supportedMediaTypes.add(MediaType.APPLICATION_RSS_XML);
        supportedMediaTypes.add(MediaType.APPLICATION_XHTML_XML);
        supportedMediaTypes.add(MediaType.APPLICATION_XML);
        supportedMediaTypes.add(MediaType.IMAGE_GIF);
        supportedMediaTypes.add(MediaType.IMAGE_JPEG);
        supportedMediaTypes.add(MediaType.IMAGE_PNG);
        supportedMediaTypes.add(MediaType.TEXT_EVENT_STREAM);
        supportedMediaTypes.add(MediaType.TEXT_HTML);
        supportedMediaTypes.add(MediaType.TEXT_MARKDOWN);
        supportedMediaTypes.add(MediaType.TEXT_PLAIN);
        supportedMediaTypes.add(MediaType.TEXT_XML);
        converter.setSupportedMediaTypes(supportedMediaTypes);
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(SerializerFeature.WriteDateUseDateFormat,
                SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.DisableCircularReferenceDetect);
        //日期格式化
        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
        converter.setFastJsonConfig(fastJsonConfig);
        return new HttpMessageConverters(converter);
    }

}
