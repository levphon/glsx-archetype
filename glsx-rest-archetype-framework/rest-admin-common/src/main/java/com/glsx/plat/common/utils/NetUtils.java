package com.glsx.plat.common.utils;

import org.apache.commons.lang.StringUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * NetUtils
 *
 * @author jin
 * @date 2018/12/4 14:44
 */
public final class NetUtils {

    private static final int PORT_MAX_SIZE = 6;

    public static boolean validateIPaddress(String ipStr) {
        if (StringUtils.isNotEmpty(ipStr)) {
            String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
            if (ipStr.matches(regex)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public static boolean validateIPport(String portStr) {
        Pattern pattern = compile("[1-9]*");
        Matcher isNum = pattern.matcher(portStr);
        if (isNum.matches() && portStr.length() < PORT_MAX_SIZE && Integer.valueOf(portStr) >= 1
                && Integer.parseInt(portStr) <= 65535) {
            return true;
        }
        return false;
    }

    public static String getLocalIp(boolean fetchLan) {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                Enumeration<InetAddress> addresses = interfaces.nextElement().getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (address.isLoopbackAddress()) continue;
                    if (address.getHostAddress().contains(":")) continue;
                    if (fetchLan) {
                        if (address.isSiteLocalAddress()) {
                            return address.getHostAddress();
                        }
                    } else {
                        if (!address.isSiteLocalAddress()) {
                            return address.getHostAddress();
                        }
                    }
                }
            }
            return fetchLan ? "127.0.0.1" : null;
        } catch (Throwable e) {
            return fetchLan ? "127.0.0.1" : null;
        }
    }
}
