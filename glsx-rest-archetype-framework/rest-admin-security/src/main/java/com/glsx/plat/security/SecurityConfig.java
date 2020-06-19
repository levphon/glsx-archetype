package com.glsx.plat.security;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    //可换为redis存储
    private final Map<String, SecurityContext> tokenCache = new ConcurrentHashMap<>();

    private static final String BEARER = "Bearer ";

    private static final String[] AUTH_WHITELIST = new String[]{"/login", "/actuator/**"};

    @Bean
    ReactiveAuthenticationManager reactiveAuthenticationManager() {
        final ReactiveUserDetailsService detailsService = userDetailsService();
        LinkedList<ReactiveAuthenticationManager> managers = new LinkedList<>();
        managers.add(authentication -> {
            //其他登陆方式(比如手机号验证码登陆)可在此设置不得抛出异常或者Mono.error
            return Mono.empty();
        });
        //必须放最后不然会优先使用用户名密码校验但是用户名密码不对时此AuthenticationManager会调用Mono.error造成后面的AuthenticationManager不生效
        managers.add(new UserDetailsRepositoryReactiveAuthenticationManager(detailsService));
        return new DelegatingReactiveAuthenticationManager(managers);
    }

    @Bean
    ServerSecurityContextRepository serverSecurityContextRepository() {
        return new ServerSecurityContextRepository() {
            @Override
            public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
                if (context.getAuthentication() instanceof TokenAuthentication) {
                    TokenAuthentication authentication = (TokenAuthentication) context.getAuthentication();
                    tokenCache.put(authentication.getToken(), context);
                }
                return Mono.empty();
            }

            @Override
            public Mono<SecurityContext> load(ServerWebExchange exchange) {
                ServerHttpRequest request = exchange.getRequest();
                String authorization = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                if (StringUtils.isEmpty(authorization) || !tokenCache.containsKey(authorization)) {
                    return Mono.empty();
                }
                return Mono.just(tokenCache.get(authorization));
            }
        };
    }

    @Bean
    SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .addFilterAt(authenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .authorizeExchange()
                .pathMatchers(AUTH_WHITELIST).permitAll()
                .anyExchange().authenticated()
                .and().build();
    }

    //修改为访问数据库的UserDetailsService即可
    @Bean
    ReactiveUserDetailsService userDetailsService() {
        User.UserBuilder userBuilder = User.withDefaultPasswordEncoder();
        UserDetails rob = userBuilder.username("rob").password("rob").roles("USER").build();
        UserDetails admin = userBuilder.username("admin").password("admin").roles("USER", "ADMIN").build();
        return new MapReactiveUserDetailsService(rob, admin);
    }

    @Bean
    ServerAuthenticationConverter serverAuthenticationConverter() {
        final AnonymousAuthenticationToken anonymous = new AnonymousAuthenticationToken("key", "anonymous", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
        return exchange -> {
            String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (StringUtils.isEmpty(token)) {
                return Mono.just(anonymous);
            }
            if (!token.startsWith(BEARER) || token.length() <= BEARER.length() || !tokenCache.containsKey(token.substring(BEARER.length()))) {
                return Mono.just(anonymous);
            }
            return Mono.just(tokenCache.get(token.substring(BEARER.length())).getAuthentication());
        };
    }

    @Bean
    AuthenticationWebFilter authenticationWebFilter() {
        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(reactiveAuthenticationManager());

        NegatedServerWebExchangeMatcher negateWhiteList = new NegatedServerWebExchangeMatcher(ServerWebExchangeMatchers.pathMatchers(AUTH_WHITELIST));
        authenticationWebFilter.setRequiresAuthenticationMatcher(negateWhiteList);

        authenticationWebFilter.setServerAuthenticationConverter(serverAuthenticationConverter());
        authenticationWebFilter.setSecurityContextRepository(serverSecurityContextRepository());

        authenticationWebFilter.setAuthenticationFailureHandler((webFilterExchange, exception) -> Mono.error(new BadCredentialsException("权限不足")));
        return authenticationWebFilter;
    }

}