package com.glsx.plat.feign.config;

import feign.Feign;
import okhttp3.ConnectionPool;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * 配置 okhttp 与连接池
 * ConnectionPool 默认创建5个线程，保持5分钟长连接
 */
@Configuration
@ConditionalOnClass(Feign.class)
@AutoConfigureBefore(FeignAutoConfiguration.class) //SpringBoot自动配置
public class OkHttpConfig {

    @Bean
    public okhttp3.OkHttpClient okHttpClient() {
        return new okhttp3.OkHttpClient.Builder()
                //设置连接超时
                .connectTimeout(10, TimeUnit.SECONDS)
                //设置读超时
                .readTimeout(10, TimeUnit.SECONDS)
                //设置写超时
                .writeTimeout(10, TimeUnit.SECONDS)
                //是否自动重连
                .retryOnConnectionFailure(true)
                .connectionPool(new ConnectionPool(10, 5L, TimeUnit.MINUTES))
                .build();
    }

//feign:
//        # feign启用hystrix，才能熔断、降级
//        # hystrix:
//        # enabled: true
//        # 启用 okhttp 关闭默认 httpclient
//    httpclient:
//        enabled: false #关闭httpclient
//        # 配置连接池
//        max-connections: 200 #feign的最大连接数
//        max-connections-per-route: 50 #fegin单个路径的最大连接数
//    okhttp:
//        enabled: true
//    # 请求与响应的压缩以提高通信效率
//    compression:
//        request:
//            enabled: true
//            min-request-size: 2048
//            mime-types: text/xml,application/xml,application/json
//        response:
//            enabled: true

}