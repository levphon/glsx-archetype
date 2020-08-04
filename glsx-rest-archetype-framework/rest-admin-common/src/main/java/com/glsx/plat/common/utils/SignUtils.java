package com.glsx.plat.common.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.*;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author
 * @version 1.0
 */
@Slf4j
public class SignUtils {

    private final static String SIGN = "sign";

    /**
     * 使用<code>secret</code>对paramValues按以下算法进行签名： <br/>
     * uppercase(hex(sha1(secretkey1value1key2value2...secret))
     *
     * @param paramValues 参数列表
     * @param secret
     * @return
     * @throws Exception
     */
    public static String sign(Map<String, String> paramValues, String secret) {
        return sign(paramValues, null, secret);
    }

    /**
     * 验签
     *
     * @param paramValues
     * @param secret
     * @return
     */
    public static boolean checkSign(Map<String, String> paramValues, String secret) {
        String paramSign = paramValues.get(SIGN);
        log.info("paramSign:{}", paramSign);
        if (StringUtils.isNullOrEmpty(paramSign)) return false;

        List<String> ignoreParamNames = new ArrayList<>(1);
        ignoreParamNames.add(SIGN);

        String localSign = sign(paramValues, ignoreParamNames, secret);
        log.info("localSign:{}", localSign);
        return paramSign.equals(localSign);
    }

    /**
     * 对paramValues进行签名，其中ignoreParamNames这些参数不参与签名
     *
     * @param paramValues
     * @param ignoreParamNames
     * @param secret
     * @return
     * @throws Exception
     */
    public static String sign(Map<String, String> paramValues, List<String> ignoreParamNames, String secret) {
        try {
            StringBuilder sb = new StringBuilder();
            List<String> paramNames = new ArrayList<>(paramValues.size());
            paramNames.addAll(paramValues.keySet());
            if (CollectionUtils.isNotEmpty(ignoreParamNames)) {
                for (String ignoreParamName : ignoreParamNames) {
                    paramNames.remove(ignoreParamName);
                }
            }
            Collections.sort(paramNames);

            sb.append(secret);
            for (String paramName : paramNames) {
                sb.append(paramName).append(paramValues.get(paramName));
            }
            sb.append(secret);
            log.info("加密前拼装的字符串:::" + sb);
//            return MD5.convert32(sb.toString());

            byte[] sha1Digest = getMD5Digest(sb.toString());
            return byte2hex(sha1Digest);
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return "";
    }

    public static String utf8Encoding(String value, String sourceCharsetName) {
        try {
            return new String(value.getBytes(sourceCharsetName), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static byte[] getSHA1Digest(String data) throws IOException {
        byte[] bytes = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            bytes = md.digest(data.getBytes("UTF-8"));
        } catch (GeneralSecurityException gse) {
            throw new IOException(gse);
        }
        return bytes;
    }

    @SuppressWarnings("unused")
    private static byte[] getMD5Digest(String data) throws IOException {
        byte[] bytes = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            bytes = md.digest(data.getBytes("UTF-8"));
        } catch (GeneralSecurityException gse) {
            throw new IOException(gse);
        }
        return bytes;
    }

    /**
     * 二进制转十六进制字符串
     *
     * @param bytes
     * @return
     */
    private static String byte2hex(byte[] bytes) {
        StringBuilder sign = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                sign.append("0");
            }
            sign.append(hex.toUpperCase());
        }
        return sign.toString();
    }

    public static String getUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().toUpperCase();
    }

    /**
     * 获取整个json结构排序的map
     *
     * @param obj
     * @return
     */
    public static TreeMap<String, String> getSignTreeMap(Object obj) {
        TreeMap<String, String> tParams = new TreeMap<>();
        Gson gson = new GsonBuilder().create();
        JsonElement jsonTree = gson.toJsonTree(obj);
        JsonSortUtil.sort(jsonTree);
        JsonObject reqJO = jsonTree.getAsJsonObject();
        Set<Map.Entry<String, JsonElement>> paramsSet = reqJO.entrySet();
        Iterator<Map.Entry<String, JsonElement>> iterator = paramsSet.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, JsonElement> entry = iterator.next();
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            if (value == null) continue;

            if (value.isJsonObject()) {
                tParams.put(key, value.toString());
            } else if (value.isJsonArray()) {
                tParams.put(key, value.toString());
            } else {
                //注意String类型用value.toString()，转的字符串会带双引号
                tParams.put(key, value.getAsString());
            }
        }
        return tParams;
    }

//    public static void main(String[] args) throws Exception {
////        System.out.printf(getUUID().replace("-", "").toLowerCase());
//
//        String custName = "杨笑";
//        String certCode = "32032119940126081X";
//        String phone = "15722887117";
//
//        Map<String, String> tparams = new TreeMap<String, String>();
//        tparams.put("custName", custName);
//        tparams.put("certCode", certCode);
//        tparams.put("phone", phone);
//
//        String sign = sign(tparams, "de0395f3be964fd4a88e2ed2eaf58284");
//    }

}

