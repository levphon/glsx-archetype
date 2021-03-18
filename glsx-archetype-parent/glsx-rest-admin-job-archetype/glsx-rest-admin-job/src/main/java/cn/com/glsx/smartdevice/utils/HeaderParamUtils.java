package cn.com.glsx.smartdevice.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouhaibao
 * @date 2021/3/5 9:24
 */
public class HeaderParamUtils {

    public static Map<String,String> buildHeaderParams(String client_id,String secret){
        long timeMillis = System.currentTimeMillis();
        String loginSign = TuyaUtils.getLoginSign(timeMillis, client_id, secret);
        Map<String, String> headerParams = new HashMap<>();
        headerParams.put("client_id", client_id);
        headerParams.put("t", timeMillis + "");
        headerParams.put("sign_method", "HMAC-SHA256");
        headerParams.put("sign", loginSign);
        return headerParams;
    }

    public static Map<String,String> buildHeaderParams(String client_id,String secret,String accessToken){
        long timeMillis = System.currentTimeMillis();
        String sign = TuyaUtils.getBussinessSign(timeMillis, client_id, secret, accessToken);
        Map<String, String> headerParams = new HashMap<>();
        headerParams.put("client_id", client_id);
        headerParams.put("t", timeMillis + "");
        headerParams.put("sign_method", "HMAC-SHA256");
        headerParams.put("sign", sign);
        headerParams.put("access_token", accessToken);
        return headerParams;
    }
}
