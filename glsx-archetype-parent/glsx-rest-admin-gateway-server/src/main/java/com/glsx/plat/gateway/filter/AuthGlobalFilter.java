package com.glsx.plat.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.glsx.plat.core.web.R;
import com.glsx.plat.exception.SystemMessage;
import com.glsx.plat.jwt.base.ComJwtUser;
import com.glsx.plat.jwt.config.JwtConfigProperties;
import com.glsx.plat.jwt.util.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author payu
 */
@Slf4j
@Component
@RefreshScope
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    @Value("${auth.skip.urls}")
    private String[] skipAuthUrls;

    @Autowired
    private JwtConfigProperties properties;

    @Autowired
    private JwtUtils<ComJwtUser> jwtUtils;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public int getOrder() {
        return -100;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String url = exchange.getRequest().getURI().getPath();
        //跳过不需要验证的路径
        if (Arrays.asList(skipAuthUrls).contains(url)) {
            return chain.filter(exchange);
        }

        //从请求头中取出token
        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        //未携带token或token在黑名单内
        if (token == null || token.isEmpty() || isBlackToken(token)) {
            return loginResponse(exchange);
        }

        //验证token
        // TODO: 2020/11/5 如果失效会报错，在失效情况下，失效刷新时间范围内，允许刷新
        DecodedJWT decodedJWT = jwtUtils.verify(token);
        if (decodedJWT == null) {
            return loginResponse(exchange);
        }

        //判断token是否快要过期或是否已经过期
        if (jwtUtils.isNeedRefreshToken(decodedJWT)) {
            //刷新token
            token = jwtUtils.refreshToken(decodedJWT);
            log.debug("refreshed token [{}]", token);
        }

        ServerHttpRequest mutableReq = appendRequestHeaders(exchange, decodedJWT.getId());

        ServerHttpResponse mutableResp = appendResponseHeaders(exchange, token);

        ServerWebExchange mutableExchange = exchange.mutate()
                .request(mutableReq)
                .response(mutableResp)
                .build();

        return chain.filter(mutableExchange);
    }

    public Mono<Void> loginResponse(ServerWebExchange exchange) {
        ServerHttpResponse originalResponse = exchange.getResponse();
        originalResponse.setStatusCode(HttpStatus.OK);
        originalResponse.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        String notLoginJson = JSON.toJSONString(R.error(SystemMessage.NOT_LOGIN.getCode(), SystemMessage.NOT_LOGIN.getMsg()));
        byte[] response = notLoginJson.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = originalResponse.bufferFactory().wrap(response);
        return originalResponse.writeWith(Flux.just(buffer));
    }

    public ServerHttpRequest appendRequestHeaders(ServerWebExchange exchange, String jwtId) {
//        //将现在的request，添加当前身份
        return exchange.getRequest().mutate()
                .header("Authorization-UserId", jwtId)
                .build();
    }

    public ServerHttpResponse appendResponseHeaders(ServerWebExchange exchange, String token) {
        ServerHttpResponse mutableResp = exchange.getResponse();
        mutableResp.getHeaders().add(HttpHeaders.AUTHORIZATION, token);
        return mutableResp;
    }

    /**
     * 判断token是否在黑名单内
     *
     * @param token
     * @return
     */
    private boolean isBlackToken(String token) {
        assert token != null;
        return stringRedisTemplate.hasKey(String.format(properties.getBlacklistKey(), token));
    }

}