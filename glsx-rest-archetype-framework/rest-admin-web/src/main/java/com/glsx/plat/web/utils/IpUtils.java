package com.glsx.plat.web.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.UnknownHostException;

@Slf4j
public class IpUtils {

    /**
     * ipv4 地址;本机
     *
     * @return
     */
    public static String ipv4() {
        try {
            String hostAddress = Inet4Address.getLocalHost().getHostAddress();
            return hostAddress;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * ipv6 地址;本机
     *
     * @return
     */
    public static String ipv6() {
        try {
            String hostAddress = Inet6Address.getLocalHost().getHostAddress();
            return hostAddress;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取IP地址
     * <p>
     * 使用Nginx等反向代理软件， 则不能通过request.getRemoteAddr()获取IP地址
     * 如果使用了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP地址，X-Forwarded-For中第一个非unknown的有效IP字符串，则为真实IP地址
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = null;
        try {
            ip = request.getHeader("x-forwarded-for");
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ip) || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("X-Real-IP");
            }
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
        } catch (Exception e) {
            log.error("IPUtils ERROR ", e);
        }

        //使用代理，则获取第一个IP地址
        if (StringUtils.isNotEmpty(ip)) {
            log.info("ip:" + ip);
            String[] ips = ip.split(",");
            if (ips.length > 0) return ips[0];
        }
        return ip;
    }

//    public static String getIpAddr(ServerHttpRequest request) {
//        String ip = null;
//        try {
//            ip = request.getHeaders().get("x-forwarded-for").get(0);
//            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
//                ip = request.getHeader("Proxy-Client-IP");
//            }
//            if (StringUtils.isEmpty(ip) || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//                ip = request.getHeader("WL-Proxy-Client-IP");
//            }
//            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
//                ip = request.getHeader("HTTP_CLIENT_IP");
//            }
//            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
//                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
//            }
//            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
//                ip = request.getRemoteAddress().getHostString();
//            }
//        } catch (Exception e) {
//            log.error("IPUtils ERROR ", e);
//        }
//
//        //使用代理，则获取第一个IP地址
//        if (StringUtils.isNotEmpty(ip)) {
//            String[] ips = ip.split(",");
//            if (ips.length > 0) return ips[0];
//        }
//        return ip;
//    }

}
