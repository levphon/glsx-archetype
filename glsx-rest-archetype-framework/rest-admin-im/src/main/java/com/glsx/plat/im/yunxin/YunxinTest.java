package com.glsx.plat.im.yunxin;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import com.glsx.plat.common.utils.SnowFlake;
import com.glsx.plat.im.yunxin.util.CheckSumBuilder;

import java.util.Date;

public class YunxinTest {

    public static void test() {

        String appKey = "94kid09c9ig9k1loimjg012345123456";
        String appSecret = "123456789012";
        String nonce = SnowFlake.nextSerialNumber();
        String curTime = String.valueOf((new Date()).getTime() / 1000L);
        String checkSum = CheckSumBuilder.getCheckSum(appSecret, nonce, curTime);//参考 计算CheckSum的java代码

        String url = "https://api.netease.im/nimserver/user/create.action";

        String result = HttpRequest.post(url)
                .header("AppKey", appKey)
                .header("Nonce", nonce)
                .header("CurTime", curTime)
                .header("CheckSum", checkSum)
                .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8")
                .execute().body();

        // 打印执行结果
        System.out.println(result);
    }

}
