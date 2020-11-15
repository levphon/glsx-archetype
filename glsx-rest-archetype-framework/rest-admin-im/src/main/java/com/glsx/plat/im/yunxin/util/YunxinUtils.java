package com.glsx.plat.im.yunxin.util;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.glsx.plat.common.utils.ObjectUtils;
import com.glsx.plat.common.utils.SnowFlake;
import com.glsx.plat.exception.ServiceException;
import com.glsx.plat.im.yunxin.YunxinConfig;
import com.glsx.plat.im.yunxin.req.AbstractYunxinRequest;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

@Slf4j
@Component
public class YunxinUtils {

    @Resource
    private YunxinConfig config;

    /**
     * 调用服务
     */
    public <T> T access(String url, Object req, Class<T> clazz) throws ServiceException {
        AbstractYunxinRequest param = (AbstractYunxinRequest) req;
        // 1. 补全请求数据
        complementedRequestData(param);
        // 2. 校验请求数据
        verifyRequestData(param);
        // 3. 网络请求,获取结果
        return realCall(url, param, clazz);
    }

    /**
     * 补全请求实体—— 补全通用的实体属性,比如appkey属性及sign属性。
     * 也可以通过 instanceof,针对特定请求实体进行补全操作。
     */
    private void complementedRequestData(AbstractYunxinRequest req) {

    }

    /**
     * 校验请求实体的通用属性,比如appkey属性及sign属性。
     * 也可以通过 instanceof,针对特定请求实体进行校验操作。
     */
    private void verifyRequestData(AbstractYunxinRequest req) {

    }

    /**
     * 真正调用网络请求方法
     *
     * @param url
     * @param param
     * @param clazz
     * @param <T>
     * @return
     */
    private <T> T realCall(String url, Object param, Class<T> clazz) throws ServiceException {
        log.info("【云信服务】http请求信息—— url[{}] param[{}]", url, JSON.toJSONString(param, true));

        String appKey = config.getAppKey();
        String appSecret = config.getAppSecret();
        String nonce = SnowFlake.nextSerialNumber();
        String curTime = String.valueOf((new Date()).getTime() / 1000L);
        String checkSum = CheckSumBuilder.getCheckSum(appSecret, nonce, curTime);//参考 计算CheckSum的java代码

        Map<String, Object> formMap = Maps.newHashMap();

        Map<String, Object> paramMap = (Map<String, Object>) ObjectUtils.objectToMap(param);
        paramMap.forEach((key, value) -> {
            if (!"class".equals(key)) {
                formMap.put(key, value);
            }
        });

        String result = HttpRequest.post(url)
                .header("AppKey", appKey)
                .header("Nonce", nonce)
                .header("CurTime", curTime)
                .header("CheckSum", checkSum)
                .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8")
                .form(formMap)
                .execute().body();
        return JSON.parseObject(result, clazz);
    }
}
