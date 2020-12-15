package com.glsx.plat.ai.baidu.face;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class FaceConfig {

    @Value("${baidu.appId:}")
    private String appId;

    @Value("${baidu.appKey:}")
    private String appKey;

    @Value("${baidu.secretKey:}")
    private String secretKey;

    @Value("${baidu.face.matchScore:80}")
    private String faceMatchScore;

}
