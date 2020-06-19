//package com.glsx.plat.webflux.filter;
//
//import com.alibaba.fastjson.JSON;
//import com.glsx.plat.common.annotation.NoLogin;
//import com.glsx.plat.common.utils.StringUtils;
//import com.glsx.plat.core.constant.BasicConstants;
//import com.glsx.plat.core.web.R;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.collections4.CollectionUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.server.reactive.ServerHttpResponse;
//import org.springframework.stereotype.Component;
//import org.springframework.web.method.HandlerMethod;
//import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;
//import org.springframework.web.server.ServerWebExchange;
//import org.springframework.web.server.WebFilter;
//import org.springframework.web.server.WebFilterChain;
//import reactor.core.publisher.Mono;
//
//import javax.servlet.http.HttpServletResponse;
//import java.io.PrintWriter;
//import java.util.List;
//
//@Slf4j
//@Component
//public class CustomWebFilter implements WebFilter {
//
//    @Autowired
//    private RequestMappingHandlerMapping requestMappingHandlerMapping;
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
//        Object handler = requestMappingHandlerMapping.getHandler(exchange).toProcessor().peek();
//
//        boolean needLoginFlag = true; // 默认需要登录
//        //注意跨域时的配置，跨域时浏览器会先发送一个option请求，这时候getHandler不会时真正的HandlerMethod
//        if (handler instanceof HandlerMethod) {
//            HandlerMethod handlerMethod = (HandlerMethod) handler;
//
//            //do your logic
//            HttpHeaders httpHeaders = exchange.getRequest().getHeaders();
//
//            // 1.先判断类上是否存在@NoLogin注解[默认无需登录注解]
//            Class<?> targetBean = handlerMethod.getBeanType();
//            NoLogin noLogin = targetBean.getAnnotation(NoLogin.class);
//            if (null != noLogin) {
//                // 如果存在,则根据注解的value()确定是否需要登录访问
//                needLoginFlag = !noLogin.value();
//            }
//            // 2.再判断调用方法上是否存在@NoLogin注解[默认无需登录注解]
//            noLogin = handlerMethod.getMethodAnnotation(NoLogin.class);
//            if (null != noLogin) {
//                // 如果存在,则根据注解的value()确定是否需要登录访问[方法覆盖类]
//                needLoginFlag = !noLogin.value();
//            }
//            // 3.需要登录访问则去验证token,反之,则通过
//            if (!needLoginFlag) {
//                //继续执行
//                return chain.filter(exchange);
//            }
//
//            // 判断是否登录
//            List<String> tokens = exchange.getRequest().getHeaders().get(BasicConstants.REQUEST_HEADERS_TOKEN);
//            // 1.判断请求是否携带token
//            if (CollectionUtils.isEmpty(tokens)) {
//                // 不存在token数据
//                log.warn("【访问拦截】用户的请求Header中未包含token信息.[请求头Authorization]");
//                return needLogin(exchange, chain);
//            }
//        }
//        //preprocess()
//        Mono<Void> response = chain.filter(exchange);
//        //postprocess()
//        return response;
//    }
//
//    private Mono<Void> needLogin(ServerWebExchange exchange, WebFilterChain chain) {
////        response.setCharacterEncoding("UTF-8");
////        response.setContentType("application/json; charset=utf-8");
////        try (PrintWriter writer = response.getWriter()) {
////            writer.write(JSON.toJSONString(R.error(ApiResultCode.ILLEGAL_ACCESS.getCode(), ApiResultCode.ILLEGAL_ACCESS.getMsg())));
////            writer.flush();
////        }
//
//        Mono<Void> response = chain.filter(exchange);
//        return response;
//    }
//
//}