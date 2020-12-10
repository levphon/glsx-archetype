package com.glsx.plat.push.service;

import com.gexin.rp.sdk.base.IBatch;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.IQueryResult;
import com.gexin.rp.sdk.base.impl.AppMessage;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.base.uitls.AppConditions;
import com.gexin.rp.sdk.exceptions.PushAppException;
import com.gexin.rp.sdk.exceptions.RequestException;
import com.gexin.rp.sdk.template.AbstractTemplate;
import com.gexin.rp.sdk.template.LinkTemplate;
import com.glsx.plat.push.properties.GetuiProperties;
import com.glsx.plat.push.template.PushTemplate;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.glsx.plat.push.constant.AppInfo.*;

@Service
public class GetuiPushService {

    @Autowired
    private GetuiProperties properties;

    /**
     * 广播到app
     */
    public String pushToApp() {
        LinkTemplate template = PushTemplate.getLinkTemplate();
        AppMessage message = new AppMessage();
        message.setData(template);
        message.setOffline(true);
        message.setOfflineExpireTime(24 * 1000 * 3600);  //离线有效时间，单位为毫秒，可选
        // 厂商下发策略；1: 个推通道优先，在线经个推通道下发，离线经厂商下发(默认);2: 在离线只经厂商下发;3: 在离线只经个推通道下发;4: 优先经厂商下发，失败后经个推通道下发;
        message.setStrategyJson("{\"default\":4,\"ios\":4,\"st\":4}");
        //全量推送时希望能控制推送速度不要太快，缓减服务器连接压力，可设置定速推送。如果未设置则按默认推送速度发送
//        message.setSpeed(100); // 设置为100，含义为个推控制下发速度在100条/秒左右

        //设置推送时间，需要申请开通套餐
//        try {
//            message.setPushTime("201907121810"); //2019年07月12日18:10分开始推送，限制条件参见官网（http://docs.getui.com/getui/server/java/push/）定时推送有关说明
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        message.setAppIdList(Lists.newArrayList(APPID));

        //推送给App的目标用户需要满足的条件
        AppConditions cdt = new AppConditions();

        //手机类型
        List<String> phoneTypeList = new ArrayList<>();
        phoneTypeList.add("IOS");
        phoneTypeList.add("ANDROID");

        //地区
        List<String> regionList = new ArrayList<>();
        //参见 datafile目录下 region_code.data
        regionList.add("33010000");//杭州市
        regionList.add("51010000");//成都市

        //自定义tag
        List<String> tagList = new ArrayList<>();
        tagList.add(TAG);
        tagList.add(TAG_2);

        //查询可推送的用户画像（需要开通VIP套餐)
        IQueryResult personaTagResult = push.getPersonaTags(APPID);
        System.out.println(personaTagResult.getResponse());

        //工作
        List<String> jobs = new ArrayList<>();
        jobs.add("0102");
        jobs.add("0110");

        //年龄
        List<String> age = new ArrayList<>();
        age.add("0000");

        //条件交并补功能, 默认是与
        cdt.addCondition(AppConditions.PHONE_TYPE, phoneTypeList, AppConditions.OptType.or);
        cdt.addCondition(AppConditions.REGION, regionList, AppConditions.OptType.or);
        cdt.addCondition(AppConditions.TAG, tagList, AppConditions.OptType.not);
        cdt.addCondition("job", jobs);
        cdt.addCondition("age", age);
        message.setConditions(cdt);

        IPushResult ret = null;
        try {
            ret = push.pushMessageToApp(message);
//            ret = push.pushMessageToApp(message, "任务别名_toApp");
        } catch (PushAppException e) {
            e.printStackTrace();
        }
        if (ret != null) {
            System.out.println(ret.getResponse().toString());
            return ret.getResponse().get("contentId").toString();
        } else {
            System.out.println("服务器响应异常");
            return null;
        }
    }

    /**
     * 对单个用户推送消息
     * <p>
     * 场景1：某用户发生了一笔交易，银行及时下发一条推送消息给该用户。
     * <p>
     * 场景2：用户定制了某本书的预订更新，当本书有更新时，需要向该用户及时下发一条更新提醒信息。
     * 这些需要向指定某个用户推送消息的场景，即需要使用对单个用户推送消息的接口。
     */
    public void pushToSingle() {
        AbstractTemplate template = PushTemplate.getTransmissionTemplate(); //通知模板(点击后续行为: 支持打开应用、发送透传内容、打开应用同时接收到透传 这三种行为)
//        AbstractTemplate template = PushTemplate.getLinkTemplate(); //点击通知打开(第三方)网页模板
//        AbstractTemplate template = PushTemplate.getTransmissionTemplate(); //透传消息模版
//        AbstractTemplate template = PushTemplate.getRevokeTemplate(); //消息撤回模版
//        AbstractTemplate template = PushTemplate.getStartActivityTemplate(); //点击通知, 打开（自身）应用内任意页面

        // 单推消息类型
        SingleMessage message = getSingleMessage(template);
        Target target = new Target();
        target.setAppId(APPID);
        target.setClientId(CID);
//        target.setAlias(ALIAS); //别名需要提前绑定

        IPushResult ret = null;
        try {
            ret = push.pushMessageToSingle(message, target);
        } catch (RequestException e) {
            e.printStackTrace();
            ret = push.pushMessageToSingle(message, target, e.getRequestId());
        }
        if (ret != null) {
            System.out.println(ret.getResponse().toString());
        } else {
            System.out.println("服务器响应异常");
        }
    }

    /**
     * 批量单推
     * <p>
     * 当单推任务较多时，推荐使用该接口，可以减少与服务端的交互次数。
     */
    private static void pushToSingleBatch() {
        IBatch batch = push.getBatch();

        IPushResult ret = null;
        try {
            //构建客户a的透传消息a
            constructClientTransMsg(CID, batch);
            //构建客户B的点击通知打开网页消息b
            constructClientLinkMsg(CID_2, batch);
            ret = batch.submit();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                ret = batch.retry();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        if (ret != null) {
            System.out.println(ret.getResponse().toString());
        } else {
            System.out.println("服务器响应异常");
        }
    }

    private static void constructClientTransMsg(String cid, IBatch batch) throws Exception {
        AbstractTemplate template = PushTemplate.getTransmissionTemplate();
        SingleMessage message = getSingleMessage(template);

        // 设置推送目标，填入appid和clientId
        Target target = new Target();
        target.setAppId(APPID);
        target.setClientId(cid);
        batch.add(message, target);
    }

    private static void constructClientLinkMsg(String cid, IBatch batch) throws Exception {
        AbstractTemplate template = PushTemplate.getLinkTemplate();
        SingleMessage message = getSingleMessage(template);

        // 设置推送目标，填入appid和clientId
        Target target = new Target();
        target.setAppId(APPID);
        target.setClientId(cid);
        batch.add(message, target);
    }

    private static SingleMessage getSingleMessage(AbstractTemplate template) {
        SingleMessage message = new SingleMessage();
        message.setData(template);
        // 设置消息离线，并设置离线时间
        message.setOffline(true);
        // 离线有效时间，单位为毫秒，可选
        message.setOfflineExpireTime(72 * 3600 * 1000);
        // 判断客户端是否wifi环境下推送。1为仅在wifi环境下推送，0为不限制网络环境，默认不限
        message.setPushNetWorkType(1);
        message.setPushNetWorkType(1); // 判断客户端是否wifi环境下推送。1为仅在wifi环境下推送，0为不限制网络环境，默认不限
        // 厂商下发策略；1: 个推通道优先，在线经个推通道下发，离线经厂商下发(默认);2: 在离线只经厂商下发;3: 在离线只经个推通道下发;4: 优先经厂商下发，失败后经个推通道下发;
        message.setStrategyJson("{\"default\":4,\"ios\":4,\"st\":4}");
        return message;
    }


}
