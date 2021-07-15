package com.glsx.plat.im.rongcloud.service;

import com.glsx.plat.im.rongcloud.util.RongCloudUtil;
import io.rong.messages.BaseMessage;
import io.rong.methods.message._private.Private;
import io.rong.methods.user.User;
import io.rong.methods.user.onlinestatus.OnlineStatus;
import io.rong.models.message.PrivateMessage;
import io.rong.models.response.CheckOnlineResult;
import io.rong.models.response.ResponseResult;
import io.rong.models.response.TokenResult;
import io.rong.models.user.UserModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class RongCloudService {

    @Autowired
    private RongCloudUtil utils;

    public TokenResult register(UserModel userModel) throws Exception {
        User user = utils.getRongCloud().user;
        return user.register(userModel);
    }

    public CheckOnlineResult checkOnline(String userId) {
        OnlineStatus onlineStatus = utils.getRongCloud().user.onlineStatus;
        try {
            return onlineStatus.check(new UserModel().setId(userId));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isOnline(String userId) {
        CheckOnlineResult result = this.checkOnline(userId);
        if (utils.isSuccess(result)) {
            assert result != null;
            return "1".equals(result.getStatus());
        }
        return false;
    }

    public void sendMessage(String senderId, List<String> targetIdList, BaseMessage message) {
        /**
         * API 文档: http://www.rongcloud.cn/docs/server_sdk_api/message/private.html#send
         *
         * 发送单聊消息<文本, 语音, 文件类型 等消息类型>
         */

        Private msgPrivate = utils.getRongCloud().message.msgPrivate;

        String[] targetIds = new String[targetIdList.size()];

        PrivateMessage privateMessage = new PrivateMessage()
                .setSenderId(senderId)
                .setTargetId(targetIdList.toArray(targetIds))
                .setObjectName(message.getType())
                .setContent(message)
                .setPushContent("")
                .setPushData("{\"pushData\":\"hello\"}")
                .setPushExt("{\"title\":\"\",\"forceShowPushContent\":0,\"pushConfigs\":{\"HW\":{\"channelId\":\"\"},\"MI\":{\"channelId\":\"\"},\"OPPO\":{\"channelId\":\"\"}}}")
                .setVerifyBlacklist(0)
                .setIsPersisted(0)
                .setIsCounted(0)
                .setIsIncludeSender(0);
        ResponseResult privateResult = null;
        try {
            privateResult = msgPrivate.send(privateMessage);
            log.info("send private getReqBody: {}", privateResult.getReqBody());
            log.info("send private message: {}", privateResult);
        } catch (Exception e) {
            log.info("send private message exception:{}", e.getMessage());
        }
    }

}
