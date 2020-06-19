package com.glsx.plat.web.interceptor;

import com.glsx.plat.common.utils.SnowFlake;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 日志链路：
 * 1、MVC Interceptor方案
 * 2、AOP 方案
 *
 * @author payu
 */
@Component
public class LogInterceptor implements HandlerInterceptor {

    private final static String REQ_ID = "REQ_ID";

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String traceId = SnowFlake.nextSerialNumber();
        MDC.put(REQ_ID, traceId);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        MDC.remove(REQ_ID);
    }

}
