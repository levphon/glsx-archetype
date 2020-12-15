package com.glsx.plat.ai.baidu.face.util;

import com.baidu.aip.face.AipFace;
import com.glsx.plat.ai.baidu.face.FaceConfig;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Desc: 人脸识别工具
 */
@Component
public class FaceUtil implements InitializingBean {

    @Autowired
    private FaceConfig faceConfig;

    private volatile AipFace client;

    public AipFace getClient() {
        return client;
    }

    @Override
    public void afterPropertiesSet() {
        client = new AipFace(faceConfig.getAppId(), faceConfig.getApiKey(), faceConfig.getSecretKey());
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);
    }

}

