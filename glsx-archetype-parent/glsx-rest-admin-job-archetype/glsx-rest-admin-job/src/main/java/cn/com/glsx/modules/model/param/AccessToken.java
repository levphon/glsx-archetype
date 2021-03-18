package cn.com.glsx.modules.model.param;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhouhaibao
 * @date 2021/3/4 10:10
 */
@Data
public class AccessToken implements Serializable {

    private String access_token;
    private Long expire_time;
    private String refresh_token;
    private String uid;
}
