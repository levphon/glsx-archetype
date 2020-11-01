package com.glsx.plat.im.yunxin;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class YunxinConfig {

    @Value("${yunxin.appKey:}")
    private String appKey;

    @Value("${yunxin.appSecret:}")
    private String appSecret;

    @Value("${yunxin.userCreate.url:https://api.netease.im/nimserver/user/create.action}")
    private String userCreateUrl;

    @Value("${yunxin.updateUinfo.url:https://api.netease.im/nimserver/user/updateUinfo.action}")
    private String updateUinfoUrl;

    @Value("${yunxin.friendAdd.url:https://api.netease.im/nimserver/friend/add.action}")
    private String friendAddUrl;

    @Value("${yunxin.sendMsg.url:https://api.netease.im/nimserver/msg/sendMsg.action}")
    private String sendMsgUrl;

    @Value("${yunxin.querySessionMsg.url:https://api.netease.im/nimserver/history/querySessionMsg.action}")
    private String querySessionMsgUrl;

    @Value("${yunxin.subscribeAdd.url:https://api.netease.im/nimserver/event/subscribe/add.action}")
    private String subscribeAddUrl;

}
