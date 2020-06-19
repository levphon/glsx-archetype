package com.glsx.plat.webflux.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * 所有/contextPath前缀的请求都会自动去除该前缀
 */
@Component
public class ContextPathFilter implements WebFilter {

    @Autowired
    private ServerProperties serverProperties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String contextPath = serverProperties.getServlet().getContextPath();
        String requestPath = exchange.getRequest().getPath().pathWithinApplication().value();
        if(contextPath != null && requestPath.startsWith(contextPath)){
            requestPath = requestPath.substring(contextPath.length());
        }
        return chain.filter(exchange.mutate().request(exchange.getRequest().mutate().path(requestPath).build()).build());
    }

}
