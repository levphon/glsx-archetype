package com.glsx.plat.web.filter;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 跨域
 *
 * @author payu
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)//控制过滤器的级别
@WebFilter(urlPatterns = "/**", filterName = "corsFilter")
public class CorsFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest reqs = (HttpServletRequest) req;

        response.setHeader("Access-Control-Allow-Origin", reqs.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, HEAD, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", HttpHeaders.AUTHORIZATION + ", Accept, Origin, X-Requested-With, Content-Type, Last-Modified, X-Custom-Header, Origin-Channel");
//        response.setHeader("Access-Control-Allow-Headers", "*");
        response.setHeader("Access-Control-Expose-Headers", HttpHeaders.AUTHORIZATION + ", X-Custom-Header, Origin-Channel");
        response.setHeader("Access-Control-Max-Age", "3600");
        if ("OPTIONS".equalsIgnoreCase(((HttpServletRequest) req).getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            filterChain.doFilter(req, res);
        }
    }

    @Override
    public void destroy() {

    }

}