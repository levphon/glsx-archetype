package com.glsx.plat.push.template;

import com.gexin.fastjson.JSONObject;
import com.gexin.rp.sdk.base.IBatch;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.base.notify.Notify;
import com.gexin.rp.sdk.base.payload.APNPayload;
import com.gexin.rp.sdk.base.payload.MultiMedia;
import com.gexin.rp.sdk.base.payload.VoIPPayload;
import com.gexin.rp.sdk.template.*;
import com.gexin.rp.sdk.template.style.Style0;

import java.util.UUID;


/**
 * 推送模板
 *
 * @author payu
 * @see
 */
public class PushTemplate {

    public static void main(String[] args) {
        getNotificationTemplate(new Notify());
//        getLinkTemplate();
//        getTransmissionTemplate();
//        getStartActivityTemplate();
//        getRevokeTemplate("XXXX");
    }

    /**
     * 点击通知打开应用模板, 在通知栏显示一条含图标、标题等的通知，用户点击后激活您的应用。
     * 通知模板(点击后续行为: 支持打开应用、发送透传内容、打开应用同时接收到透传 这三种行为)
     *
     * @param notify
     * @return
     */
    public static NotificationTemplate getNotificationTemplate(Notify notify) {
        NotificationTemplate template = new NotificationTemplate();
        //设置展示样式
        template.setStyle(PushStyle.getStyle0());
        template.setTransmissionType(1);  // 透传消息设置，收到消息是否立即启动应用： 1为立即启动，2则广播等待客户端自启动
        template.setTransmissionContent(notify.getContent());
//        template.setSmsInfo(PushSmsInfo.getSmsInfo()); //短信补量推送

//        template.setDuration("2019-07-09 11:40:00", "2019-07-09 12:24:00");  // 设置定时展示时间，安卓机型可用
        template.setNotifyid(123); // 在消息推送的时候设置notifyid。如果需要覆盖此条消息，则下次使用相同的notifyid发一条新的消息。客户端sdk会根据notifyid进行覆盖。
        template.setAPNInfo(getAPNPayload()); //ios消息推送
        return template;
    }

    /***
     * 安卓和ios在线个推
     * @param notify
     * @return
     */
    public static NotificationTemplate onlineNotificationTemplate(Notify notify) {
        //这里可以看到在线和离线使用的不同类进行处理
        NotificationTemplate template = new NotificationTemplate();

        Style0 style = new Style0();
        //设置通知栏标题与内容
        style.setTitle(notify.getTitle());
        style.setText(notify.getContent());
        // 配置通知栏图标
        // style.setLogo("icon.png");
        // 配置通知栏网络图标
        style.setLogoUrl("http://img.schyxgl.com/201908201619esc.png");
        // 设置通知是否响铃，震动，或者可清除
        style.setRing(true);
        style.setVibrate(true);
        style.setClearable(true);
        //style.setChannel("通知渠道id");
        //style.setChannelName("通知渠道名称");
        style.setChannelLevel(3);//设置通知渠道重要性
        //安卓
        template.setStyle(style);

        //ios处理
        APNPayload payload = new APNPayload();
        payload.setAutoBadge("+1");
        payload.setContentAvailable(1);
        payload.setSound("default");

//        APNPayload.DictionaryAlertMsg alertMsg = new APNPayload.DictionaryAlertMsg();
//        alertMsg.setTitle(title);
//        alertMsg.setBody(text);
//        payload.setAlertMsg(alertMsg);

        MultiMedia multiMedia = new MultiMedia();
        multiMedia.setResUrl("http://img.schyxgl.com/201908201619esc.png");
        multiMedia.setResType(MultiMedia.MediaType.pic);
        multiMedia.setOnlyWifi(false);
        multiMedia.setResId(UUID.randomUUID().toString());
        payload.addMultiMedia(multiMedia);

        //ios
        template.setAPNInfo(payload);
        //透传消息接受方式设置，1：立即启动APP，2：客户端收到消息后需要自行处理
        template.setTransmissionType(1);
        //template.setTransmissionContent("请输入您要透传的内容");
        template.setTransmissionContent(notify.getContent());

        return template;
    }

    /***
     * 离线模板处理，厂商通道
     * @param notify
     * @param paramJson
     * @return
     */
    public static TransmissionTemplate offLineTransmissionTemplate(Notify notify, JSONObject paramJson) {
        //厂商通道离线推送注意类的使用，在线和离线使用不同
        TransmissionTemplate template = new TransmissionTemplate();
        template.set3rdNotifyInfo(notify);//设置第三方通知

        //ios处理通知消息
        APNPayload payload = new APNPayload();
        payload.setAutoBadge("+1");
        payload.setContentAvailable(0);
        payload.setSound("default");

        for (String key : paramJson.keySet()) {
            payload.addCustomMsg(key, paramJson.getString(key));
        }

        APNPayload.DictionaryAlertMsg alertMsg = new APNPayload.DictionaryAlertMsg();
        alertMsg.setTitle(notify.getTitle());
        alertMsg.setBody(notify.getContent());
        payload.setAlertMsg(alertMsg);

        //ios
        template.setAPNInfo(payload);
        //这里也可设置穿透内容
        template.setTransmissionContent(paramJson.toString());
        template.setTransmissionType(1);

        return template;
    }

    /**
     * 点击通知打开(第三方)网页模板, 在通知栏显示一条含图标、标题等的通知，用户点击可打开您指定的网页。
     *
     * @param notifyId
     * @param url
     * @return
     */
    public static LinkTemplate getLinkTemplate(Integer notifyId, String url) {
        LinkTemplate template = new LinkTemplate();
        //设置展示样式
        template.setStyle(PushStyle.getStyle0());
        //在消息推送的时候设置notifyid。如果需要覆盖此条消息，则下次使用相同的notifyid发一条新的消息。客户端sdk会根据notifyid进行覆盖。
        template.setNotifyid(notifyId);
        // 设置打开的网址地址
        template.setUrl(url);
//         template.setSmsInfo(PushSmsInfo.getSmsInfo()); //短信补量推送
//        template.setDuration("2019-07-09 11:40:00", "2019-07-09 12:24:00");  // 设置定时展示时间，安卓机型可用
        return template;
    }

    /**
     * 透传消息模版,透传消息是指消息传递到客户端只有消息内容，展现形式由客户端自行定义。客户端可自定义通知的展现形式，也可自定义通知到达之后的动作，或者不做任何展现。
     *
     * @param notify
     * @return
     */
    public static TransmissionTemplate getTransmissionTemplate(Notify notify) {
        TransmissionTemplate template = new TransmissionTemplate();

        //透传消息设置，1为强制启动应用，客户端接收到消息后就会立即启动应用；2为等待应用启动
        template.setTransmissionType(1);
        template.setTransmissionContent(notify.getContent()); //透传内容
        template.setAPNInfo(getAPNPayload()); //ios消息推送
//        template.setAPNInfo(getVoIPPayload());
//        template.setSmsInfo(PushSmsInfo.getSmsInfo()); //短信补量推送

        template.set3rdNotifyInfo(notify);//设置第三方通知
        return template;
    }

    /**
     * 点击通知, 打开（自身）应用内任意页面
     *
     * @return
     */
    public static StartActivityTemplate getStartActivityTemplate() {
        StartActivityTemplate template = new StartActivityTemplate();
        //设置展示样式
        template.setStyle(PushStyle.getStyle0());

        String intent = "intent:#Intent;component=com.yourpackage/.NewsActivity;end";
        template.setIntent(intent); //最大长度限制为1000
        template.setNotifyid(123); // 在消息推送的时候设置notifyid。如果需要覆盖此条消息，则下次使用相同的notifyid发一条新的消息。客户端sdk会根据notifyid进行覆盖。
//        template.setSmsInfo(PushSmsInfo.getSmsInfo()); //短信补量推送
//        template.setDuration("2019-07-09 11:40:00", "2019-07-09 12:24:00");  // 设置定时展示时间，安卓机型可用
        return template;
    }

    /**
     * 消息撤回模版
     *
     * @param taskId
     * @return
     */
    public static RevokeTemplate getRevokeTemplate(String taskId) {
        RevokeTemplate template = new RevokeTemplate();
        template.setOldTaskId(taskId); //指定需要撤回消息对应的taskId
        template.setForce(false); // 客户端没有找到对应的taskid，是否把对应appid下所有的通知都撤回
        return template;
    }

    private static APNPayload getAPNPayload() {
        APNPayload payload = new APNPayload();
        //在已有数字基础上加1显示，设置为-1时，在已有数字上减1显示，设置为数字时，显示指定数字
        payload.setAutoBadge("+1");
        payload.setContentAvailable(1);
        //ios 12.0 以上可以使用 Dictionary 类型的 sound
        payload.setSound("default");
        payload.setCategory("$由客户端定义");
        payload.addCustomMsg("由客户自定义消息key", "由客户自定义消息value");
        //简单模式APNPayload.SimpleMsg
        // payload.setAlertMsg(new APNPayload.SimpleAlertMsg("hello"));
        // payload.setAlertMsg(getDictionaryAlertMsg());  //字典模式使用APNPayload.DictionaryAlertMsg

        //设置语音播报类型，int类型，0.不可用 1.播放body 2.播放自定义文本
        payload.setVoicePlayType(2);
        //设置语音播报内容，String类型，非必须参数，用户自定义播放内容，仅在voicePlayMessage=2时生效
        //注：当"定义类型"=2, "定义内容"为空时则忽略不播放
        payload.setVoicePlayMessage("定义内容");

        // 添加多媒体资源
        payload.addMultiMedia(new MultiMedia()
                .setResType(MultiMedia.MediaType.pic)
                .setResUrl("资源文件地址")
                .setOnlyWifi(true));

        return payload;
    }

    private static APNPayload.DictionaryAlertMsg getDictionaryAlertMsg() {
        APNPayload.DictionaryAlertMsg alertMsg = new APNPayload.DictionaryAlertMsg();
        alertMsg.setBody("body1");
        alertMsg.setActionLocKey("显示关闭和查看两个按钮的消息");
        alertMsg.setLocKey("loc-key");
        alertMsg.addLocArg("loc-ary1");
        alertMsg.addLocArg("loc-ary2");
        alertMsg.setLaunchImage("调用已经在应用程序中绑定的图形文件名");
        // iOS8.2以上版本支持
        alertMsg.setTitle("通知标题");
        alertMsg.setTitleLocKey("自定义通知标题");
        alertMsg.addTitleLocArg("自定义通知标题组1");
        alertMsg.addTitleLocArg("自定义通知标题组2");

        alertMsg.setSubtitle("sub-title");
        alertMsg.setSubtitleLocKey("sub-loc-key1");
        alertMsg.addSubtitleLocArgs("sub-loc-arg1");
        alertMsg.addSubtitleLocArgs("sub-loc-arg2");
        return alertMsg;
    }

    /**
     * 需要使用iOS语音传输，请使用VoIPPayload代替APNPayload
     *
     * @return
     */
    private static VoIPPayload getVoIPPayload() {
        VoIPPayload payload = new VoIPPayload();
        JSONObject jo = new JSONObject();
        jo.put("key1", "value1");
        payload.setVoIPPayload(jo.toString());
        return payload;
    }

    /**
     * 构建批量推送透传消息
     *
     * @param appId
     * @param cid
     * @param batch
     * @throws Exception
     */
    public static void constructClientTransMsg(String appId, String cid, IBatch batch, Notify notify) throws Exception {
        AbstractTemplate template = PushTemplate.getTransmissionTemplate(notify);
        SingleMessage message = getSingleMessage(template);

        // 设置推送目标，填入appid和clientId
        Target target = new Target();
        target.setAppId(appId);
        target.setClientId(cid);
        batch.add(message, target);
    }

    /**
     * 构建批量推送带链接消息
     *
     * @param appId
     * @param cid
     * @param batch
     * @throws Exception
     */
    public static void constructClientLinkMsg(String appId, String cid, IBatch batch, Notify notify) throws Exception {
        AbstractTemplate template = PushTemplate.getLinkTemplate(notify.getNotifyId(), notify.getUrl());
        SingleMessage message = getSingleMessage(template);

        // 设置推送目标，填入appid和clientId
        Target target = new Target();
        target.setAppId(appId);
        target.setClientId(cid);
        batch.add(message, target);
    }

    public static SingleMessage getSingleMessage(AbstractTemplate template) {
        SingleMessage message = new SingleMessage();
        message.setData(template);
        // 设置消息离线，并设置离线时间
        message.setOffline(true);
        // 离线有效时间，单位为毫秒，可选
        message.setOfflineExpireTime(72 * 3600 * 1000);
        // 判断客户端是否wifi环境下推送。1为仅在wifi环境下推送，0为不限制网络环境，默认不限
        message.setPushNetWorkType(1);
        // 厂商下发策略；1: 个推通道优先，在线经个推通道下发，离线经厂商下发(默认);2: 在离线只经厂商下发;3: 在离线只经个推通道下发;4: 优先经厂商下发，失败后经个推通道下发;
        message.setStrategyJson("{\"default\":4,\"ios\":4,\"st\":4}");
        return message;
    }

}
