/**
 *
 */
package com.glsx.plat.common.utils;

import java.io.*;
import java.util.*;

import static java.util.Objects.isNull;

/**
 * @data 2012-7-6 上午10:50:01
 * @
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

    /**
     * 取两字符数组的合集
     *
     * @param originalList
     * @param newList
     * @return
     */
    public static String[] union(String[] originalList, String[] newList) {
        Set<String> set = new HashSet<String>();
        for (String str : originalList) {
            set.add(str);
        }
        for (String str : newList) {
            set.add(str);
        }
        String[] result = {};
        return set.toArray(result);
    }

    /**
     * 替换指定分隔符的字符串,注使用正则特殊分隔需转义
     * <pre>
     * StringUtils.replaceCharSplitStr("1,2,22,3,4", "2", ",")  = 1,22,3,4
     * StringUtils.replaceCharSplitStr("1#2#22#3#4", "2", "#")  = 1#22#3#4
     * </pre>
     *
     * @param oldStr        原字符串
     * @param replaceStr    替换字符串
     * @param separatorChar 分隔字符
     * @return
     */
    public static String replaceCharSplitStr(String oldStr, String replaceStr, String separatorChar) {
        if (isEmpty(oldStr) || isEmpty(replaceStr)) {
            return oldStr;
        }
        oldStr = separatorChar + oldStr + separatorChar;
        oldStr = oldStr.replaceAll(separatorChar + replaceStr + separatorChar, separatorChar);
        if (isNotEmpty(oldStr) && oldStr.length() > 1) {
            return oldStr.substring(1, oldStr.length() - 1);
        }
        return "";
    }

    public static boolean isNullOrEmpty(String str) {
        if (null == str || "".equals(str)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 位数不足补0
     *
     * @param message
     * @param size
     * @return
     */
    public static String zeroFill(String message, int size) {
        int length = message.length();
        if (length >= size) {
            return message;
        } else {
            String zeroStr = "";
            int differLength = size - length;
            for (int i = 1; i <= differLength; i++) {
                zeroStr += "0";
            }
            return zeroStr + message;
        }
    }

    /**
     * 返回中英文字符串的字节长度
     *
     * @param str
     * @return
     */
    public static int getStrLength(String str) {
        if (str == null || str.length() < 0) {
            return 0;
        }
        int len = 0;
        char c;
        for (int i = str.length() - 1; i >= 0; i--) {
            c = str.charAt(i);
            if (c > 255) {
                /**//*
                 * GBK 编码格式 中文占两个字节
                 * UTF-8 编码格式中文占三个字节 len += 3;
                 */
                len += 3;
            } else {
                len++;
            }
        }
        return len;
    }

    public static String replacePartOfStr(String oldStr, String replaceStrOrChar, int start, int end) {
        StringBuilder sb = new StringBuilder(oldStr);
        StringBuilder asterisk = new StringBuilder();
        for (int i = 0; i < sb.length() - (start + end); i++) {
            asterisk.append(replaceStrOrChar);
        }
        sb.replace(start, sb.length() - end, asterisk.toString());
        return sb.toString();
    }

    public static String convertStreamToString(InputStream is) throws UnsupportedEncodingException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder builder = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                builder.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return builder.toString();
    }

    /**
     * 将null转空字符串
     * @param origin
     * @return
     */
    public static String covertNullToEmptyStr(String origin) {
        if (origin == null) return "";
        return origin;
    }

    /**
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     *
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
    public static String createLinkString(Map<String, String> params) {
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        String prestr = "";
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }
        return prestr;
    }

    /**
     * 创建指定数量的随机字符串
     *
     * @param numberFlag 是否是数字
     * @param length
     * @return
     */
    public static String generateRandomCode(boolean numberFlag, int length) {
        String retStr = "";
        String strTable = numberFlag ? "1234567890" : "1234567890abcdefghijkmnpqrstuvwxyz";
        int len = strTable.length();
        boolean bDone = true;
        do {
            retStr = "";
            int count = 0;
            for (int i = 0; i < length; i++) {
                double dblR = Math.random() * len;
                int intR = (int) Math.floor(dblR);
                char c = strTable.charAt(intR);
                if (('0' <= c) && (c <= '9')) {
                    count++;
                }
                retStr += strTable.charAt(intR);
            }
            if (count >= 2) {
                bDone = false;
            }
        } while (bDone);
        return retStr;
    }

    /**
     * 获取参数不为空值
     *
     * @param value defaultValue 要判断的value
     * @return value 返回值
     */
    public static <T> T nvl(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }

    /**
     * * 判断一个字符串是否为非空串
     *
     * @param str String
     * @return true：非空串 false：空串
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }


    /**
     * * 判断一个对象数组是否为空
     *
     * @param objects 要判断的对象数组
     *                * @return true：为空 false：非空
     */
    public static boolean isEmpty(Object[] objects) {
        return isNull(objects) || (objects.length == 0);
    }

    /**
     * 是否包含字符串
     *
     * @param str  验证字符串
     * @param strs 字符串组
     * @return 包含返回true
     */
    public static boolean inStringIgnoreCase(String str, String... strs) {
        if (str != null && strs != null) {
            for (String s : strs) {
                if (str.equalsIgnoreCase(trim(s))) {
                    return true;
                }
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object obj) {
        return (T) obj;
    }

//    public static void main(String[] args) throws UnsupportedEncodingException {
//        System.out.println("中文a".getBytes("UTF-8").length); //7
//        System.out.println(getStrLength("中文a")); //7
//        System.out.println("中文a".length()); //3
//    }

}
