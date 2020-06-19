package com.glsx.plat.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Reactor HTTP Request Body 被 subscribe 一次后，下次 subscribe 的结果为空。这是因为 Reactor 将 HTTP Request Body 的发布源头 为FluxReceive ，这是一个动态发布者，发布内容为 HTTP Request 的消息体。因此，当 HTTP Request 的 消息体 被 subscribe 一次后，后续所有的 subscribe 都是 空。因为 HTTP Request 的消息体只发送一次。
 * <p>
 * 如果想多次获取 HTTP Request Body，就需要在第一次 subscribe 时，将 Request Body 缓存起来，这样后续 subscribe 时直接从缓存获取 body 信息即可。
 *
 * @author payu
 */
@Slf4j
@Component
public class BodyCacheWebFilter implements WebFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
//        1、使用自定义的装饰类实现表体缓存
        BodyCacheServerWebExchange exchangeDecorator = new BodyCacheServerWebExchange(exchange);
        return chain.filter(exchangeDecorator);

//        2、Spring Cloud Gateway 缓存表体的实现（报错？）
//        return ServerWebExchangeUtils.cacheRequestBody(exchange, (serverHttpRequest) ->
//                chain.filter(exchange.mutate().request(serverHttpRequest).build())).switchIfEmpty(chain.filter(exchange));
    }

    @Override
    public int getOrder() {
        return -1;
    }

}
