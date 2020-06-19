package com.glsx.plat.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.glsx.plat.common.utils.SignUtils;
import com.glsx.plat.core.web.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.multipart.Part;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.*;

/**
 * 验签
 *
 * @author payu
 */
@Slf4j
@Component
public class CheckSignFilter implements GlobalFilter, Ordered {

    @Value("${gateway.sign.switch:off}")
    private String signSwitch;

    @Value("${gateway.sign.secret}")
    private String signSecret;

    //保存HttpMessageReader
    private static final List<HttpMessageReader<?>> MESSAGE_READERS = HandlerStrategies.withDefaults().messageReaders();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        log.info("访问地址：{}", request.getURI().toString());

        // 请求参数上的url地址
        Map<String, String> params = new HashMap<>();
        request.getQueryParams().forEach((key, items) -> {
            params.put(key, items.get(0));
        });
        if (HttpMethod.GET.name().equals(request.getMethodValue())) {
            return this.checkSign(params, chain, exchange);
        } else if (HttpMethod.POST.name().equals(request.getMethodValue())) {
            return DataBufferUtils.join(exchange.getRequest().getBody()).flatMap(dataBuffer -> {
                DataBufferUtils.retain(dataBuffer);
                final Flux<DataBuffer> cachedFlux = Flux.defer(() -> Flux.just(dataBuffer.slice(0, dataBuffer.readableByteCount())));
                final ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
                    @Override
                    public Flux<DataBuffer> getBody() {
                        return cachedFlux;
                    }
                };
                final ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();

                return cacheBody(mutatedExchange, chain, params);
            });

        }
        return chain.filter(exchange);
    }

    /***
      * 验证签名
      * @author Lance lance_lan_2016@163.com
      * @date 2020-01-07 09:57
      * @param params
      * @param chain
      * @param exchange
      * @return reactor.core.publisher.Mono<java.lang.Void>
      *
      * */
    private Mono<Void> checkSign(Map<String, String> params, GatewayFilterChain chain, ServerWebExchange exchange) {
        if (!"on".equalsIgnoreCase(signSwitch)) return chain.filter(exchange);

        log.info("校验参数集合：" + params);
        if (!SignUtils.checkSign(params, signSecret)) {
            // 返回json格式
            exchange.getResponse().setStatusCode(HttpStatus.OK);
            exchange.getResponse().getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);
            return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(JSON.toJSONString(R.error(HttpStatus.FORBIDDEN.value(), "签名验证失败")).getBytes())));
        }
        return chain.filter(exchange);
    }

    @SuppressWarnings("unchecked")
    private Mono<Void> cacheBody(ServerWebExchange exchange, GatewayFilterChain chain, Map<String, String> params) {
        final HttpHeaders headers = exchange.getRequest().getHeaders();
        if (headers.getContentLength() == 0) {
            return chain.filter(exchange);
        }
        final ResolvableType resolvableType;
        if (MediaType.MULTIPART_FORM_DATA.isCompatibleWith(headers.getContentType())) {
            resolvableType = ResolvableType.forClassWithGenerics(MultiValueMap.class, String.class, Part.class);
        } else if (MediaType.APPLICATION_JSON.isCompatibleWith(headers.getContentType()) || MediaType.APPLICATION_JSON_UTF8.isCompatibleWith(headers.getContentType())) {
            resolvableType = ResolvableType.forClass(JSONObject.class);//必须是Object不能是Array，带sign字段
        } else {
            resolvableType = ResolvableType.forClass(String.class);
        }

        return MESSAGE_READERS.stream().filter(reader -> reader.canRead(resolvableType,
                exchange.getRequest().getHeaders().getContentType())).findFirst()
                .orElseThrow(() -> new IllegalStateException("no suitable HttpMessageReader.")).readMono(resolvableType,
                        exchange.getRequest(), Collections.emptyMap()).flatMap(resolvedBody -> {
                    if (resolvedBody instanceof MultiValueMap) {
                        @SuppressWarnings("rawtypes")
                        MultiValueMap<String, Object> map = (MultiValueMap) resolvedBody;
                        map.keySet().forEach(key -> {
                            // SynchronossPartHttpMessageReader
                            Object obj = map.get(key);
                            List<Object> list = (List<Object>) obj;
                            for (Object object : list) {
                                if (object.getClass().toString().equals("class org.springframework.http.codec.multipart.SynchronossPartHttpMessageReader$SynchronossFilePart")) {
                                    continue;
                                }
                                Field[] fields = object.getClass().getDeclaredFields();
                                try {
                                    for (Field field : fields) {
                                        field.setAccessible(true);
                                        params.put(key, field.get(object) + "");
                                    }
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                    log.info(e.getLocalizedMessage());
                                }
                            }
                        });
                    } else if (resolvedBody instanceof JSONObject) {
                        @SuppressWarnings("rawtypes")
                        JSONObject bodyObj = (JSONObject) resolvedBody;

                        Iterator iter = bodyObj.entrySet().iterator();
                        while (iter.hasNext()) {
                            Map.Entry entry = (Map.Entry) iter.next();
                            params.put(entry.getKey().toString(), String.valueOf(entry.getValue()));
                        }
                    } else {
                        if (null != resolvedBody) {
                            String path = null;
                            try {
                                path = URLDecoder.decode(resolvedBody.toString(), "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                                log.error(e.getLocalizedMessage());
                            }
                            if (null != path) {
                                String items[] = path.split("&");
                                for (String item : items) {
                                    String subItems[] = item.split("=");
                                    if (subItems.length == 2) {
                                        params.put(subItems[0], subItems[1]);
                                    }
                                }
                            }
                        }
                    }
                    return this.checkSign(params, chain, exchange);
                });
    }

    @Override
    public int getOrder() {
        return -20;
    }

}
