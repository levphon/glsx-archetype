package com.glsx.plat.web.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;

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
                if (ip.equals("127.0.0.1") || ip.equals("0:0:0:0:0:0:0:1")) {
                    //根据网卡取本机配置的IP
                    InetAddress inet = null;
                    try {
                        inet = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    ip = inet.getHostAddress();
                }
            }
        } catch (Exception e) {
            log.error("IPUtils ERROR ", e);
        }

        //使用代理，则获取第一个IP地址
        if (StringUtils.isNotEmpty(ip)) {
            String[] ips = ip.split(",");
            if (ips.length > 0) return ips[0];
        }
        //替换ipv6本地地址
        String realIp = "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
        return realIp;
    }

    /**
     * 百度接口
     * 通过用户ip获取用户所在地
     *
     * @param strIP
     * @return
     */
    public static String getAddressByBD2(String strIP) {
        if ("127.0.0.1".equals(strIP)) {
            return strIP;
        }
        try {
            URL url = new URL("http://opendata.baidu.com/api.php?query=" + strIP + "&co=&resource_id=6006&t=1433920989928&ie=utf8&oe=utf-8&format=json");
            URLConnection conn = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            String line = null;
            StringBuffer result = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();
            JSONObject jsStr = JSON.parseObject(result.toString());
            JSONArray jsData = (JSONArray) jsStr.get("data");
            JSONObject data = (JSONObject) jsData.get(0);//位置
            return (String) data.get("location");
        } catch (Exception e) {
            return strIP + "解析失败";
        }
    }

//    public static void main(String[] args) {
//        System.out.println(getAddressByBD2("127.0.0.1"));
//    }

}
