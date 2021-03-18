package cn.com.glsx.smartdevice.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

/**
 * 获取涂鸦签名
 *
 * @author zhouhaibao
 * @date 2021/3/3 14:23
 */
public class TuyaUtils {

    /**
     * 获取令牌签名
     *
     * @param time      时间戳
     * @param client_id client_id
     * @param secret    secret
     * @return
     */
    public static String getLoginSign(long time, String client_id, String secret) {
        String hash=null;
        try {
            String message = client_id + time;
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] bytes = sha256_HMAC.doFinal(message.getBytes());
            hash = new HexBinaryAdapter().marshal(bytes).toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hash;
    }

    /**
     * 获取业务的签名
     *
     * @param time        时间戳
     * @param client_id   client_id
     * @param secret      secret
     * @param accessToken 令牌
     * @return
     */
    public static String getBussinessSign(long time, String client_id, String secret, String accessToken) {
        String hash=null;
        try {
            String message = client_id + accessToken + time;
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] bytes = sha256_HMAC.doFinal(message.getBytes());
            hash = new HexBinaryAdapter().marshal(bytes).toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hash;
    }
}
