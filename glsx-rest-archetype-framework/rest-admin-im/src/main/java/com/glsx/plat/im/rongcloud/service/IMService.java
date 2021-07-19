package com.glsx.plat.im.rongcloud.service;

import com.glsx.plat.im.rongcloud.exception.CusException;
import com.glsx.plat.im.rongcloud.message.MediaMessage;
import io.rong.messages.BaseMessage;

/**
 * IM相关操作
 */
public interface IMService {

    /**
     * 注册IM用户
     *
     * @param id
     * @param name
     * @param portrait
     * @return
     * @throws CusException
     */
    boolean addUser(String id, String name, String portrait) throws CusException;

    /**
     * 修改IM用户信息
     *
     * @param id
     * @param name
     * @param portrait
     * @return
     * @throws CusException
     */
    boolean updateUser(String id, String name, String portrait) throws CusException;

    /**
     * 单聊模块 发送文本、图片、图文消息
     *
     * @param fromId      发送人 Id
     * @param targetIds   接收人 Id
     * @param msg         消息体
     * @param pushContent push 内容, 分为两类 内置消息 Push 、自定义消息 Push
     * @param pushData    iOS 平台为 Push 通知时附加到 payload 中，Android 客户端收到推送消息时对应字段名为 pushData
     * @return
     * @throws CusException
     */
    boolean sendPrivateMsg(String fromId, String[] targetIds, BaseMessage msg, String pushContent, String pushData) throws CusException;

    /**
     * 系统消息，发送给多人
     *
     * @param fromId      发送人 Id
     * @param targetIds   接收方 Id
     * @param msg         消息
     * @param msg         消息内容
     * @param pushContent push 内容, 分为两类 内置消息 Push 、自定义消息 Push
     * @param pushData    iOS 平台为 Push 通知时附加到 payload 中，Android 客户端收到推送消息时对应字段名为 pushData
     * @return
     * @throws CusException
     */
    boolean sendSystemMax100Msg(String fromId, String[] targetIds, BaseMessage msg, String pushContent, String pushData) throws CusException;

    /**
     * 发送消息给系统所有人
     *
     * @param fromId
     * @param msg
     * @param pushContent
     * @param pushData
     * @return
     * @throws CusException
     */
    boolean sendSystemBroadcastMsg(String fromId, BaseMessage msg, String pushContent, String pushData) throws CusException;

    /**
     * 获取融云token
     *
     * @param userId
     * @param name
     * @param portraitUri
     * @return
     * @throws CusException
     */
    String getToken(String userId, String name, String portraitUri) throws CusException;


    /**
     * 单聊模块  发送自定义消息
     *
     * @param fromId      发送人 Id
     * @param targetIds   接收人 Id
     * @param msg         自定义 消息体
     * @param pushContent 定义显示的 Push 内容，如果 objectName 为融云内置消息类型时，则发送后用户一定会收到 Push 信息
     * @param pushData    针对 iOS 平台为 Push 通知时附加到 payload 中
     * @return
     * @throws CusException
     */
    boolean sendUserDefinedMsg(String fromId, String[] targetIds, MediaMessage msg, String pushContent, String pushData) throws CusException;

    /**
     * 检查用户在线状态方法
     * 调用频率：每秒钟限 100 次
     *
     * @param userId
     * @return
     * @throws CusException
     */
    Integer checkOnline(String userId) throws CusException;

}