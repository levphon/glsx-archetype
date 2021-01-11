package com.glsx.plat.push.template;

import com.gexin.fastjson.JSON;
import com.gexin.fastjson.JSONObject;
import com.gexin.rp.sdk.base.impl.AppMessage;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.notify.Notify;
import com.gexin.rp.sdk.base.payload.APNPayload;
import com.gexin.rp.sdk.base.payload.MultiMedia;
import com.gexin.rp.sdk.base.payload.VoIPPayload;
import com.gexin.rp.sdk.dto.GtReq;
import com.gexin.rp.sdk.template.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.UUID;


/**
 * 推送模板
 *
 * @author payu
 * @see
 */
@Slf4j
public class PushTemplate {

    /***
     * 安卓和ios在线个推
     * 点击通知打开应用模板, 在通知栏显示一条含图标、标题等的通知，用户点击后激活您的应用。
     * 通知模板(点击后续行为: 支持打开应用、发送透传内容、打开应用同时接收到透传 这三种行为)
     * @param notify
     * @return
     */
    public static NotificationTemplate onlineNotificationTemplate(Notify notify) {
        //这里可以看到在线和离线使用的不同类进行处理
        NotificationTemplate template = new NotificationTemplate();
        //安卓设置展示样式
        template.setStyle(PushStyle.getStyle0(notify));

        //ios处理
        APNPayload payload = getSimpleAlertMsgAPNPayload(notify, new JSONObject());

        // 在消息推送的时候设置notifyid。如果需要覆盖此条消息，则下次使用相同的notifyid发一条新的消息。客户端sdk会根据notifyid进行覆盖。
        template.setNotifyid(notify.getNotifyId());
        //ios
        template.setAPNInfo(payload);
        //透传消息接受方式设置，1：立即启动APP，2：客户端收到消息后需要自行处理
        template.setTransmissionType(2);
        //设置透传内容
        template.setTransmissionContent(notify.getContent());
        //短信补量推送
        //template.setSmsInfo(PushSmsInfo.getSmsInfo());
        return template;
    }

    /***
     * 离线模板处理，厂商通道
     * 透传消息模版,透传消息是指消息传递到客户端只有消息内容，展现形式由客户端自行定义。客户端可自定义通知的展现形式，也可自定义通知到达之后的动作，或者不做任何展现。
     * @param notify
     * @param paramJson
     * @return
     */
    public static TransmissionTemplate offlineTransmissionTemplate(Notify notify, JSONObject paramJson) {
        //厂商通道离线推送注意类的使用，在线和离线使用不同
        TransmissionTemplate template = new TransmissionTemplate();
        //设置第三方通知
        template.set3rdNotifyInfo(notify);
        //ios
        //透传消息设置，1为强制启动应用，客户端接收到消息后就会立即启动应用；2为等待应用启动
        template.setTransmissionType(2);
        //设置穿透内容
        template.setTransmissionContent(notify.getContent());
        //ios消息推送
        template.setAPNInfo(getSimpleAlertMsgAPNPayload(notify, paramJson));
        //短信补量推送
        //template.setSmsInfo(PushSmsInfo.getSmsInfo());
        return template;
    }

    /**
     * 获取同时有Android第三方推送及IOS推送功能的很透传消息
     *
     * @param title       标题
     * @param body        正文
     * @param badge       IOS的角标数
     * @param customParam 自定义属性
     * @return
     */
    public static TransmissionTemplate getTransmissionTemplateWith3rdNotifyInfoAndAPNPayload(String title,
                                                                                             String body,
                                                                                             String badge,
                                                                                             Map<String, String> customParam) {
        TransmissionTemplate template = new TransmissionTemplate();

        // 透传消息设置，1为强制启动应用，客户端接收到消息后就会立即启动应用；2为等待应用启动
        template.setTransmissionType(2);

        // 透传内容
        String transmissionContent = JSON.toJSONString(customParam);

        log.info("TransmissionContent:{}", transmissionContent);

        template.setTransmissionContent(transmissionContent);

        Notify notify = get3rdNotifyInfo(title, body, customParam);

        log.info("3rdNotifyInfo:{}", notify.toString());

        // 第三方厂商推送
        template.set3rdNotifyInfo(notify);

        // 针对IOS消息推送，设置APNs
        template.setAPNInfo(getAPNPayload(title, body, badge, customParam));

        return template;
    }

    /**
     * 第三方厂商通知
     *
     * @param title   标题
     * @param content 正文
     * @param payload 附带属性
     * @return
     */
    private static Notify get3rdNotifyInfo(String title, String content, Map<String, String> payload) {
        Notify notify = new Notify();
        notify.setTitle(title);
        notify.setContent(content);
        notify.setType(GtReq.NotifyInfo.Type._intent);

        String intent = getIntent(title, content, payload);
        log.info("Intent:{}", intent);
        notify.setIntent(intent);

        String payloadJson = JSON.toJSONString(payload);
        log.info("Payload:{}", payloadJson);
        notify.setPayload(payloadJson);

        return notify;
    }

    /**
     * 生成intent
     *
     * @param title
     * @param content
     * @param payload
     * @return
     */
    private static String getIntent(String title, String content, Map<String, String> payload) {
        String intent = "intent:#Intent;launchFlags=0x04000000;action=android.intent.action.oppopush;package=${packageName};component=${packageName}/io.dcloud.PandoraEntry;S.UP-OL-SU=true;S.title=${title};S.content=${content};S.payload=${payload};end";
        intent = intent.replace("${packageName}", payload.get(GetuiMessage.PACKAGE_NAME))
                .replace("${title}", title)
                .replace("${content}", content)
                .replace("${payload}", JSON.toJSONString(payload));
        return intent;
    }

    /**
     * IOS的APNs消息
     *
     * @param title
     * @param body
     * @param badge
     * @param customMsg
     * @return
     */
    private static APNPayload getAPNPayload(String title, String body, String badge, Map<String, String> customMsg) {
        APNPayload payload = new APNPayload();
        // 在已有数字基础上加1显示，设置为-1时，在已有数字上减1显示，设置为数字时，显示指定数字
        if (badge != null && badge.trim().length() > 0) {
            payload.setAutoBadge(badge);
        }
        payload.setContentAvailable(0);
        // ios 12.0 以上可以使用 Dictionary 类型的 sound
        // 通知铃声文件名，无声设置为"com.gexin.ios.silence"
        payload.setSound("default");
//      payload.setCategory("$由客户端定义");
        if (customMsg != null) {
            for (Map.Entry<String, String> entry : customMsg.entrySet()) {
                payload.addCustomMsg(entry.getKey(), entry.getValue());
            }
        }
//      payload.setAlertMsg(new APNPayload.SimpleAlertMsg("helloCCCC"));//简单模式APNPayload.SimpleMsg
        payload.setAlertMsg(getDictionaryAlertMsg(title, body)); // 字典模式使用APNPayload.DictionaryAlertMsg

//      // 设置语音播报类型，int类型，0.不可用 1.播放body 2.播放自定义文本
//      payload.setVoicePlayType(2);
//      // 设置语音播报内容，String类型，非必须参数，用户自定义播放内容，仅在voicePlayMessage=2时生效
//      // 注：当"定义类型"=2, "定义内容"为空时则忽略不播放
//      payload.setVoicePlayMessage("定义内容");
//
//      // 添加多媒体资源
//      payload.addMultiMedia(new MultiMedia().setResType(MultiMedia.MediaType.pic).setResUrl("资源文件地址").setOnlyWifi(true));
        return payload;
    }

    /**
     * IOS通知提示样式
     *
     * @param title
     * @param body
     * @return
     */
    private static APNPayload.DictionaryAlertMsg getDictionaryAlertMsg(String title, String body) {
        APNPayload.DictionaryAlertMsg alertMsg = new APNPayload.DictionaryAlertMsg();
        alertMsg.setBody(body);
//      alertMsg.setActionLocKey("显示关闭和查看两个按钮的消息");
//      alertMsg.setLocKey("loc-key1");
//      alertMsg.addLocArg("loc-ary1");
//      alertMsg.setLaunchImage("调用已经在应用程序中绑定的图形文件名");
        // iOS8.2以上版本支持
        alertMsg.setTitle(title);
//      alertMsg.setTitleLocKey("自定义通知标题");
//      alertMsg.addTitleLocArg("自定义通知标题组");
        return alertMsg;
    }

    /**
     * 点击通知打开(第三方)网页模板, 在通知栏显示一条含图标、标题等的通知，用户点击可打开您指定的网页。
     *
     * @param title
     * @param body
     * @param customParam
     * @return
     */
    public static LinkTemplate getLinkTemplate(String title,
                                               String body,
                                               Map<String, String> customParam) {

        Notify notify = getLinkNotifyInfo(title, body, customParam);

        LinkTemplate template = new LinkTemplate();
        //设置展示样式
        template.setStyle(PushStyle.getStyle0(notify));
        //在消息推送的时候设置notifyid。如果需要覆盖此条消息，则下次使用相同的notifyid发一条新的消息。客户端sdk会根据notifyid进行覆盖。
        template.setNotifyid(notify.getNotifyId());
        // 设置打开的网址地址
        template.setUrl(notify.getUrl());
        // 短信补量推送
        //template.setSmsInfo(PushSmsInfo.getSmsInfo());
        // 设置定时展示时间，安卓机型可用
        //template.setDuration("2019-07-09 11:40:00", "2019-07-09 12:24:00");
        return template;
    }

    /**
     * 第三方厂商通知
     *
     * @param title   标题
     * @param content 正文
     * @param payload 附带属性
     * @return
     */
    private static Notify getLinkNotifyInfo(String title, String content, Map<String, String> payload) {
        Notify notify = new Notify();
        notify.setTitle(title);
        notify.setContent(content);
        notify.setType(GtReq.NotifyInfo.Type._url);
        notify.setUrl(payload.get("url"));
        return notify;
    }

    /**
     * 点击通知, 打开（自身）应用内任意页面
     *
     * @return
     */
    public static StartActivityTemplate getStartActivityTemplate(Notify notify) {
        StartActivityTemplate template = new StartActivityTemplate();
        //设置展示样式
        template.setStyle(PushStyle.getStyle0(notify));

        String intent = notify.getIntent();
        //最大长度限制为1000
        template.setIntent(intent);
        // 在消息推送的时候设置notifyid。如果需要覆盖此条消息，则下次使用相同的notifyid发一条新的消息。客户端sdk会根据notifyid进行覆盖。
        template.setNotifyid(notify.getNotifyId());
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

    private static APNPayload getSimpleAlertMsgAPNPayload(Notify notify, JSONObject paramJson) {
        //ios处理通知消息
        APNPayload payload = new APNPayload();
        //在已有数字基础上加1显示，设置为-1时，在已有数字上减1显示，设置为数字时，显示指定数字
        payload.setAutoBadge("+1");
        payload.setContentAvailable(0);
        //ios 12.0 以上可以使用 Dictionary 类型的 sound
        payload.setSound("default");
        payload.setCategory("$由客户端定义");
        for (String key : paramJson.keySet()) {
            payload.addCustomMsg(key, paramJson.getString(key));
        }

        //简单模式APNPayload.SimpleMsg
        payload.setAlertMsg(new APNPayload.SimpleAlertMsg("推送异常..."));
        return payload;
    }

    private static APNPayload getMultiMediaAPNPayload(Notify notify, JSONObject paramJson) {
        //ios处理通知消息
        APNPayload payload = new APNPayload();
        //在已有数字基础上加1显示，设置为-1时，在已有数字上减1显示，设置为数字时，显示指定数字
        payload.setAutoBadge("+1");
        payload.setContentAvailable(0);
        //ios 12.0 以上可以使用 Dictionary 类型的 sound
        payload.setSound("default");
        payload.setCategory("$由客户端定义");
        for (String key : paramJson.keySet()) {
            payload.addCustomMsg(key, paramJson.getString(key));
        }

        //简单模式APNPayload.SimpleMsg
        payload.setAlertMsg(new APNPayload.SimpleAlertMsg(notify.getContent()));

        MultiMedia multiMedia = new MultiMedia();
        multiMedia.setResUrl("http://img.schyxgl.com/201908201619esc.png");
        multiMedia.setResType(MultiMedia.MediaType.pic);
        multiMedia.setOnlyWifi(false);
        multiMedia.setResId(UUID.randomUUID().toString());
        payload.addMultiMedia(multiMedia);

        return payload;
    }

    /**
     * 需要使用iOS语音传输，请使用VoIPPayload代替APNPayload
     *
     * @return
     */
    private static VoIPPayload getVoIPPayload(JSONObject paramJson) {
        VoIPPayload payload = new VoIPPayload();
        payload.setVoIPPayload(paramJson.toString());
        return payload;
    }

    public static SingleMessage getSingleMessage(AbstractTemplate template) {
        SingleMessage message = new SingleMessage();
        message.setData(template);
        // 设置消息离线，并设置离线时间
        message.setOffline(true);
        // 离线有效时间，单位为毫秒，可选
        message.setOfflineExpireTime(72 * 3600 * 1000);
        // 判断客户端是否wifi环境下推送。1为仅在wifi环境下推送，0为不限制网络环境，默认不限
        message.setPushNetWorkType(0);
        // 厂商下发策略；1: 个推通道优先，在线经个推通道下发，离线经厂商下发(默认);2: 在离线只经厂商下发;3: 在离线只经个推通道下发;4: 优先经厂商下发，失败后经个推通道下发;
        message.setStrategyJson("{\"default\":4,\"ios\":4,\"st\":4}");
        return message;
    }

    public static AppMessage getAppMessage(AbstractTemplate template) {
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
        return message;
    }

}
