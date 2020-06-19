package com.glsx.plat.web.filter;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 参数空格过滤器,去掉参数两边的空格
 */
@Component
@Order(1)
@WebFilter(urlPatterns = "/**", filterName = "trimFilter", dispatcherTypes = DispatcherType.REQUEST)
public class TrimFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        TrimRequestWrapper parmsRequest = new TrimRequestWrapper((HttpServletRequest) servletRequest);
        filterChain.doFilter(parmsRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
