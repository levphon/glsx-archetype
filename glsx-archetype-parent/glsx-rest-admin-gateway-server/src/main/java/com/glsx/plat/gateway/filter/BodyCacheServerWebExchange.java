package com.glsx.plat.gateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;

/**
 * @author payu
 */
public class BodyCacheServerWebExchange extends ServerWebExchangeDecorator {

    private final ServerHttpRequest serverHttpRequest;

    public BodyCacheServerWebExchange(ServerWebExchange delegate) {
        super(delegate);

        serverHttpRequest = new BodyCacheServerHttpRequestDecorator(delegate.getRequest());
    }

    @Override
    public ServerHttpRequest getRequest() {
        return serverHttpRequest;
    }

}
