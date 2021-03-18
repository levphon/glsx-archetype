package cn.com.glsx.smartdevice.job;

import cn.com.glsx.modules.model.param.AccessToken;
import cn.com.glsx.smartdevice.Common.Constants;
import cn.com.glsx.smartdevice.model.ResponseBody;
import cn.com.glsx.smartdevice.redis.AccessTokenRedis;
import cn.com.glsx.smartdevice.utils.HeaderParamUtils;
import cn.com.glsx.smartdevice.utils.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.elasticjob.lite.annotation.ElasticSimpleJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * @author zhouhaibao
 * @date 2021/3/8 13:57
 */
@Slf4j
@Component
@ElasticSimpleJob(jobName = "GetTokenJob",cron="0/10 * * * * ?")
public class GetTokenJob implements SimpleJob {

    @Autowired
    private AccessTokenRedis accessTokenRedis;

    @Value("${tuya.tokenFlashUrl}")
    private String tokenFlashUrl;

    @Value("${tuya.getTokenUrl}")
    private String getTokenUrl;

    @Value("${tuya.client_id}")
    private String client_id;

    @Value("${tuya.secret}")
    private String secret;

    public ResponseBody sendRequestGetOA(String requesturl) {
        ResponseBody responseBody = null;
        Map<String, String> headerParams = HeaderParamUtils.buildHeaderParams(client_id, secret);
        try {
            JSONObject jsonObject = HttpUtil.sendGet(requesturl, null, headerParams);
            responseBody = JSON.toJavaObject(jsonObject, ResponseBody.class);
        } catch (IOException e) {
            e.printStackTrace();
            log.info("请求用户信息接口异常,异常信息{}", e);
        }
        return responseBody;
    }

    @Override
    public void execute(ShardingContext shardingContext) {
        AccessToken accessToken = accessTokenRedis.readCache(Constants.ACCESSTOKEN_REDISKEY);
        if (null != accessToken) {
            if ((System.currentTimeMillis()-accessToken.getExpire_time()) >= 1000 * 60 * 60 * 2) {
                ResponseBody responseBody = sendRequestGetOA(tokenFlashUrl + accessToken.getRefresh_token());
                if (responseBody != null && responseBody.isSuccess()) {
                    String result =  responseBody.getResult();
                    AccessToken token = JSONObject.parseObject(result, AccessToken.class);
                    token.setExpire_time(System.currentTimeMillis());
                    accessTokenRedis.add(Constants.ACCESSTOKEN_REDISKEY, token);
                } else {
                    log.info("调用刷新令牌接口异常");
                }
            }
        } else {
            ResponseBody responseBody = sendRequestGetOA(getTokenUrl);
            if (responseBody != null && responseBody.isSuccess()) {
                String result =  responseBody.getResult();
                AccessToken token = JSONObject.parseObject(result, AccessToken.class);
                token.setExpire_time(System.currentTimeMillis());
                accessTokenRedis.add(Constants.ACCESSTOKEN_REDISKEY, token);
            } else {
                log.info("调用获取accesstoken接口异常");
            }
        }
    }
}
