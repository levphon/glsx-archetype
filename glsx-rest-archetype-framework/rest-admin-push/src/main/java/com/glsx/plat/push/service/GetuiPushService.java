package com.glsx.plat.push.service;

import com.gexin.fastjson.JSONObject;
import com.gexin.rp.sdk.base.IBatch;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.AppMessage;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.base.notify.Notify;
import com.gexin.rp.sdk.base.uitls.AppConditions;
import com.gexin.rp.sdk.exceptions.PushAppException;
import com.gexin.rp.sdk.exceptions.RequestException;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.AbstractTemplate;
import com.gexin.rp.sdk.template.LinkTemplate;
import com.glsx.plat.push.properties.GetuiProperties;
import com.glsx.plat.push.template.PushTemplate;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author payu
 */
@Service
public class GetuiPushService implements InitializingBean {

    public volatile IGtPush push;

    @Autowired
    private GetuiProperties properties;

//    public static void main(String[] args) {
//        String cid = "dc5610a356cf1c4b3de6b6dcdf04b0cd";
//
//        GetuiPushService pushService = new GetuiPushService();
//
//        Notify notify = new Notify();
//        notify.setTitle("test");
//        notify.setContent("this is a test");
//        notify.setType(GtReq.NotifyInfo.Type._intent);
//        String intent = "intent:#Intent;launchFlags=0x10000000;package=io.yue.app;component=你的包名 /com.getui.demo.MainActivity;i.parm1=12;end";
//        notify.setIntent(intent);
//
////      pushService.uniPushToApp();
//        pushService.uniPushToSingle(cid, notify);
//        pushService.uniPushOnline(cid, notify);
//        pushService.uniPushOffLine(cid, notify, new JSONObject());
//    }

    /**
     * 批量单推
     * <p>
     * 当单推任务较多时，推荐使用该接口，可以减少与服务端的交互次数。
     */
    private void uniPushToSingleBatch(Notify notify, String... cids) {
        IBatch batch = push.getBatch();

        //构建点击通知打开网页消息
        AbstractTemplate template = PushTemplate.getLinkTemplate(notify);
        //构建透传消息
        //AbstractTemplate template = PushTemplate.offLineTransmissionTemplate(notify, new JSONObject());

        SingleMessage message = PushTemplate.getSingleMessage(template);

        IPushResult ret = null;
        try {
            for (int i = 0; i < cids.length; i++) {
                // 设置推送目标，填入appid和clientId
                Target target = new Target();
                target.setAppId(properties.getAppId());
                target.setClientId(cids[i]);
                batch.add(message, target);
            }
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

    /***
     * unipush 在线推送
     * @param cid
     * @param notify
     */
    public void uniPushOnline(String cid, Notify notify) {
        AbstractTemplate template = PushTemplate.onlineNotificationTemplate(notify);
//        AbstractTemplate template = PushTemplate.getLinkTemplate(); //点击通知打开(第三方)网页模板
//        AbstractTemplate template = PushTemplate.getTransmissionTemplate(); //透传消息模版
//        AbstractTemplate template = PushTemplate.getRevokeTemplate(); //消息撤回模版
//        AbstractTemplate template = PushTemplate.getStartActivityTemplate(); //点击通知, 打开（自身）应用内任意页面

        uniPush(cid, template);
    }

    /***
     * 离线通道推送主方法,对单个用户推送消息
     * <p>
     * 场景1：某用户发生了一笔交易，银行及时下发一条推送消息给该用户。
     * <p>
     * 场景2：用户定制了某本书的预订更新，当本书有更新时，需要向该用户及时下发一条更新提醒信息。
     * 这些需要向指定某个用户推送消息的场景，即需要使用对单个用户推送消息的接口。
     * @param cid
     * @param notify
     * @param paramJson
     */
    public void uniPushOffLine(String cid, Notify notify, JSONObject paramJson) {
        AbstractTemplate template = PushTemplate.offLineTransmissionTemplate(notify, paramJson);
//        AbstractTemplate template = PushTemplate.getLinkTemplate(); //点击通知打开(第三方)网页模板
//        AbstractTemplate template = PushTemplate.getTransmissionTemplate(); //透传消息模版
//        AbstractTemplate template = PushTemplate.getRevokeTemplate(); //消息撤回模版
//        AbstractTemplate template = PushTemplate.getStartActivityTemplate(); //点击通知, 打开（自身）应用内任意页面

        uniPush(cid, template);
    }

    /**
     * 执行推送
     *
     * @param cid
     * @param template
     */
    public void uniPush(String cid, AbstractTemplate template) {
        // 设置APPID与APPKEY
        template.setAppId(properties.getAppId());
        template.setAppkey(properties.getAppKey());

        SingleMessage message = PushTemplate.getSingleMessage(template);

        Target target = new Target();
        target.setAppId(properties.getAppId());
        target.setClientId(cid);

        IPushResult ret = null;
        try {
            ret = push.pushMessageToSingle(message, target);
        } catch (RequestException e) {
            e.printStackTrace();
            ret = push.pushMessageToSingle(message, target, e.getRequestId());
        }
        if (ret != null) {
            System.out.println("推送成功，第三方返回：" + ret.getResponse().toString());
        } else {
            System.out.println("推送时第三方服务器响应异常");
        }
    }

    /**
     * 广播到app
     *
     * @param notify
     * @return
     */
    public String uniPushToApp(Notify notify) {
        LinkTemplate template = PushTemplate.getLinkTemplate(notify);

        // 设置APPID与APPKEY
        template.setAppId(properties.getAppId());
        template.setAppkey(properties.getAppKey());

        AppMessage message = PushTemplate.getAppMessage(template);
        message.setAppIdList(Lists.newArrayList(properties.getAppId()));

        //推送给App的目标用户需要满足的条件
        AppConditions cdt = new AppConditions();

        //手机类型
        List<String> phoneTypeList = new ArrayList<>();
        phoneTypeList.add("IOS");
        phoneTypeList.add("ANDROID");

        //地区
//        List<String> regionList = new ArrayList<>();
//        //参见 datafile目录下 region_code.data
//        regionList.add("33010000");//杭州市
//        regionList.add("51010000");//成都市

        //自定义tag
//        List<String> tagList = new ArrayList<>();
//        tagList.add(TAG);
//        tagList.add(TAG_2);

        //查询可推送的用户画像（需要开通VIP套餐)
//        IQueryResult personaTagResult = push.getPersonaTags(APPID);
//        System.out.println(personaTagResult.getResponse());

        //条件交并补功能, 默认是与
        cdt.addCondition(AppConditions.PHONE_TYPE, phoneTypeList, AppConditions.OptType.or);
//        cdt.addCondition(AppConditions.REGION, regionList, AppConditions.OptType.or);
//        cdt.addCondition(AppConditions.TAG, tagList, AppConditions.OptType.not);
        message.setConditions(cdt);

        IPushResult ret = null;
        try {
            ret = push.pushMessageToApp(message);
//            ret = push.pushMessageToApp(message, "任务别名_toApp");
        } catch (PushAppException e) {
            e.printStackTrace();
        }
        if (ret != null) {
            System.out.println("推送成功，第三方返回：" + ret.getResponse().toString());
            return ret.getResponse().get("contentId").toString();
        } else {
            System.out.println("推送时第三方服务器响应异常");
            return null;
        }
    }

    @Override
    public void afterPropertiesSet() {
        push = new IGtPush(properties.getAppKey(), properties.getMasterSecret());
    }

}
