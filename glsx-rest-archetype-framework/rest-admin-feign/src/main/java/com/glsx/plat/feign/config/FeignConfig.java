package com.glsx.plat.feign.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import feign.Logger;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.*;
import org.springframework.http.MediaType;

import java.util.ArrayList;
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
    public Encoder EncoderfeignEncoder() {
        return new SpringEncoder(feignHttpMessageConverter());
    }

    @Bean
    public Decoder DecoderfeignDecoder() {
        return new SpringDecoder(feignHttpMessageConverter());
    }

    private ObjectFactory<HttpMessageConverters> feignHttpMessageConverter() {
        final HttpMessageConverters httpMessageConverters = new HttpMessageConverters(getFastJsonConverter());
        return () -> httpMessageConverters;
    }

    private FastJsonHttpMessageConverter getFastJsonConverter() {
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        List<MediaType> supportedMediaTypes = new ArrayList<>();
        MediaType mediaTypeJson = MediaType.valueOf(MediaType.APPLICATION_JSON_UTF8_VALUE);
        supportedMediaTypes.add(mediaTypeJson);
        converter.setSupportedMediaTypes(supportedMediaTypes);
        FastJsonConfig config = new FastJsonConfig();
        config.setSerializerFeatures(SerializerFeature.DisableCircularReferenceDetect);
        converter.setFastJsonConfig(config);
        return converter;
    }

}
