package com.glsx.plat.ai.baidu.baidu.service;

import com.alibaba.fastjson.JSONObject;
import com.glsx.plat.ai.baidu.baidu.model.BaiduErrorCode;
import com.glsx.plat.ai.baidu.baidu.model.FaceLivenessResp;
import com.glsx.plat.ai.baidu.baidu.model.PersonVerifyResp;
import com.glsx.plat.common.utils.StringUtils;
import com.glsx.plat.redis.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class BaiduService {

    @Value("${baidu.ai.face.apiKey}")
    private String apiKey;

    @Value("${baidu.ai.face.secretKey}")
    private String secretKey;

    @Autowired
    private RedisUtils redisUtils;

    private final static String REDIS_BAIDU_ACCESS_TOKEN_KEY = "";


    /**
     * 获取权限token
     *
     * @return 返回示例：
     * {
     * "access_token": "24.460da4889caad24cccdb1fea17221975.2592000.1491995545.282335-1234567",
     * "expires_in": 2592000
     * }
     */
    public String getAuth() throws Exception {
        String access_token = (String) redisUtils.get(REDIS_BAIDU_ACCESS_TOKEN_KEY);
        if (StringUtils.isNullOrEmpty(access_token)) {
            // 官网获取的 API Key 更新为你注册的
            // 官网获取的 Secret Key 更新为你注册的
            return getToken(apiKey, secretKey);
        }
        return access_token;
    }

    /**
     * 获取API访问token
     * 该token有一定的有效期，需要自行管理，当失效时需重新获取.
     *
     * @param ak - 百度云官网获取的 API Key
     * @param sk - 百度云官网获取的 Securet Key
     * @return assess_token 示例：
     * "24.460da4889caad24cccdb1fea17221975.2592000.1491995545.282335-1234567"
     */
    private String getToken(String ak, String sk) throws Exception {
        // 获取token地址
        String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
        String getAccessTokenUrl = authHost
                // 1. grant_type为固定参数
                + "grant_type=client_credentials"
                // 2. 官网获取的 API Key
                + "&client_id=" + ak
                // 3. 官网获取的 Secret Key
                + "&client_secret=" + sk;
        String result = get(getAccessTokenUrl);
        log.info("获取token接口返回：" + result);
        JSONObject jsonObject = JSONObject.parseObject(result);
        String access_token = jsonObject.getString("access_token");
        long expires_in = jsonObject.getLongValue("expires_in");
        redisUtils.setex(REDIS_BAIDU_ACCESS_TOKEN_KEY, access_token, expires_in);
        return access_token;
    }

    public PersonVerifyResp personVerify(String name, String idCardNumber, String image) throws Exception {
        // 获取token
        String access_token = getAuth();

        PersonVerifyResp resp = new PersonVerifyResp();

        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/person/verify";
        String personVerifyUrl = url + "?access_token=" + access_token;

        Map<String, String> headerParams = new HashMap<>();
        headerParams.put("Content-Type", "application/x-www-form-urlencoded");

        Map<String, String> params = new HashMap<>();
        params.put("image", "sfasq35sadvsvqwr5q...");
        params.put("name", name);
        params.put("id_card_number", idCardNumber);
        if (image.contains("http") || image.contains("HTTP")) {
            params.put("image_type", "URL");
        } else {
            params.put("image_type", "BASE64");
        }
        log.info("身份验证接口：" + personVerifyUrl);
        String result = post(personVerifyUrl, params, headerParams);
        resp.setResult(result);

        JSONObject jsonObject = JSONObject.parseObject(result);
        log.info("身份验证接口返回结果：" + jsonObject.toJSONString());

        String errorMsg = jsonObject.getString("error_msg");
        if (StringUtils.isNullOrEmpty(errorMsg)) errorMsg = jsonObject.getString("err_msg");
        resp.setMessage(errorMsg);

        if ("SUCCESS".equalsIgnoreCase(errorMsg) && jsonObject.containsKey("result")) {
            jsonObject = jsonObject.getJSONObject("result");
            log.info("身份验证接口返回结果：" + jsonObject.toJSONString());

            float threshold = 80;
            float score = jsonObject.getFloatValue("score");
            resp.setSuccess(score > threshold);
            resp.setMessage(resp.isSuccess() ? "身份验证比对通过；" : "身份验证比对未通过；");
        }
        return resp;
    }

    /**
     * 语音校验码接口
     *
     * @return
     */
    public JSONObject getSessionCode() throws Exception {
        // 获取token
        String access_token = getAuth();
        String url = "https://aip.baidubce.com/rest/2.0/face/v1/faceliveness/sessioncode?";
        String sessionCodeUrl = url + "access_token=" + access_token;
        String result = get(sessionCodeUrl);
        log.info("获取随机数接口返回：" + result);
        JSONObject jsonObject = JSONObject.parseObject(result);
        if ("SUCCESS".equalsIgnoreCase(jsonObject.getString("err_msg")) && jsonObject.containsKey("result")) {
            jsonObject = jsonObject.getJSONObject("result");
//            jsonObject.put("access_token", access_token);//太长，不入库了
            return jsonObject;
        }
        return null;
    }

    /**
     * 语音校验码接口
     *
     * @param sessionId
     * @param videoBase64
     * @return
     */
    public FaceLivenessResp verify(String sessionId, String videoBase64) throws Exception {

        FaceLivenessResp resp = new FaceLivenessResp();

        // 获取token
        String token = getAuth();

        String url = "https://aip.baidubce.com/rest/2.0/face/v1/faceliveness/verify?";
        String verifyUrl = url + "access_token=" + token;

        Map<String, String> headerParams = new HashMap<>();
        headerParams.put("Content-Type", "application/x-www-form-urlencoded");

        Map<String, String> params = new HashMap<>();
        params.put("session_id", sessionId);
        params.put("video_base64", videoBase64);

        log.info("活体验证接口：" + verifyUrl);
        String result = post(verifyUrl, params, headerParams);

        resp.setResult(result);

        JSONObject jsonObject = JSONObject.parseObject(result);
        jsonObject.remove("pic_list");
        //log.info("验证接口返回结果：" + jsonObject.toJSONString());

        String errorCode = jsonObject.getString("err_code");
        String errorMsg = jsonObject.getString("err_msg");
        if (StringUtils.isNullOrEmpty(errorMsg)) errorMsg = jsonObject.getString("error_msg");

        resp.setMessage(errorMsg);
        log.info("errorCode:" + errorCode + ",errorMsg:" + errorMsg);

        if ("SUCCESS".equalsIgnoreCase(errorMsg) && jsonObject.containsKey("result")) {
            jsonObject = jsonObject.getJSONObject("result");
            log.info("验证接口返回result内容：" + jsonObject.toJSONString());

            double score = jsonObject.getDoubleValue("score");
            JSONObject thresholdsJO = jsonObject.getJSONObject("thresholds");
            double frr_1e4 = thresholdsJO.getDouble("frr_1e-4");//万分之一误识别率的阈值
            double frr_1e3 = thresholdsJO.getDouble("frr_1e-3");//千分之一误识别率的阈值
            double frr_1e2 = thresholdsJO.getDouble("frr_1e-2");//百分之一误识别率的阈值

            double threshold = 0.393241;
            resp.setSuccess(score > threshold);
            resp.setMessage(resp.isSuccess() ? "活体采集人像与数据源的比对通过；" : "活体采集人像与数据源的比对未通过；");
        } else {
            errorMsg = BaiduErrorCode.getErrorMsg(errorMsg);
            resp.setMessage(errorMsg);
        }
        return resp;
    }

    private String post(String url, Map<String, String> params, Map<String, String> headers) throws IOException {
        int timeout = 30;
        List<NameValuePair> paramList = new ArrayList<>();
        if (params != null) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                paramList.add(new BasicNameValuePair(param.getKey(), param.getValue()));
            }
        }
        HttpClientBuilder httpBuilder = HttpClients.custom();
        CloseableHttpClient httpClient = httpBuilder.build();
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, HTTP.UTF_8);
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(timeout * 1000)
                .setConnectTimeout(timeout * 1000)
                .setConnectionRequestTimeout(timeout * 1000)
                .setCookieSpec("aip.baidubce.com")
                .build();
        HttpUriRequest reqMethod = RequestBuilder.post().setUri(url).setEntity(entity).setConfig(requestConfig).build();
        if (headers != null) {
            for (Map.Entry<String, String> param : headers.entrySet()) {
                reqMethod.setHeader(param.getKey(), param.getValue());
            }
        }
        HttpResponse response = httpClient.execute(reqMethod);
        String retVal = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
        return retVal;
    }

    /**
     * get请求
     *
     * @param url
     * @return
     * @throws Exception
     */
    public String get(String url) throws Exception {
        int timeout = 30;
        HttpClientBuilder httpBuilder = HttpClients.custom();
        CloseableHttpClient httpClient = httpBuilder.build();
        List<NameValuePair> paramList = new ArrayList<>();
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, HTTP.UTF_8);
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(timeout * 1000)
                .setConnectTimeout(timeout * 1000)
                .setConnectionRequestTimeout(timeout * 1000)
                .setCookieSpec("aip.baidubce.com")
                .build();
        HttpUriRequest reqMethod = RequestBuilder.get().setUri(url).setEntity(entity).setConfig(requestConfig).build();

        HttpResponse response = httpClient.execute(reqMethod);
        String retVal = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
        return retVal;
    }

}
