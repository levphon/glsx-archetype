package com.glsx.plat.im.rongcloud;

import io.rong.RongCloud;
import io.rong.methods.user.User;
import io.rong.methods.user.onlinestatus.OnlineStatus;
import io.rong.models.response.CheckOnlineResult;
import io.rong.models.response.TokenResult;
import io.rong.models.user.UserModel;

public class RongCloudTest {

    public static void main(String[] args) throws Exception {
        String appKey = "bmdehs6pbah5s";
        String appSecret = "MXAsaitIKQRm1";

        RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret);
        User user = rongCloud.user;

        /**
         * API 文档: http://www.rongcloud.cn/docs/server_sdk_api/user/user.html#register
         *
         * 注册用户，生成用户在融云的唯一身份标识 Token
         */
//        UserModel userModel = new UserModel()
//                .setId("324428586138271744")
//                .setName("RongCloud")
//                .setPortrait("http://www.rongcloud.cn/images/logo.png");
//        TokenResult result = user.register(userModel);
//        System.out.println("getToken:  " + result.toString());
        //{"token":"6A1b1WLvnq5iUSX3eyR2p2PuVyJo+H2GA8tenjeXNz8=@puwy.cn.rongnav.com;puwy.cn.rongcfg.com","userId":"hHjap87","code":200}

        OnlineStatus onlineStatus = rongCloud.user.onlineStatus;
        CheckOnlineResult onlineResult = onlineStatus.check(new UserModel().setId("324428586138271744"));
        System.out.println("onlineResult:  " + onlineResult.toString());
    }

}
