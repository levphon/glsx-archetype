package com.glsx.plat.web.utils;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * session 工具,主要用来获取用户信息
 */
public class SessionUtils {

    /**
     * 获取当前  request
     *
     * @return
     */
    public static HttpServletRequest request() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    /**
     * 获取当前  response
     *
     * @return
     */
    public static HttpServletResponse response() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
    }

    /**
     * 获取当前 session
     *
     * @return
     */
    public static HttpSession session() {
        return request().getSession();
    }

    /**
     * 获取当前用户
     *
     * @return
     */
    public static Object getUser(String key) {
        HttpServletRequest request = request();
        HttpSession session = request.getSession();
        Object user = session.getAttribute(key);
        return user;
    }

    /**
     * 设置 session 信息
     *
     * @param user
     */
    public static <T> void setSessionUser(String key, T user) {
        Class<?> clazz = user.getClass();
        HttpSession session = request().getSession();
        session.setAttribute(key, user);
    }

    /**
     * 删除session
     */
    public static void removeSession(String key) {
        HttpSession session = request().getSession();
        session.removeAttribute(key);
        session.invalidate();
    }

}
