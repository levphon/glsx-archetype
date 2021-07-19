package com.glsx.plat.im.rongcloud.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.glsx.plat.im.rongcloud.exception.CusException;
import com.glsx.plat.im.rongcloud.message.MediaMessage;
import com.glsx.plat.im.rongcloud.util.RongCloudUtil;
import de.taimos.httputils.HTTPRequest;
import de.taimos.httputils.HTTPResponse;
import de.taimos.httputils.WS;
import io.rong.messages.BaseMessage;
import io.rong.methods.message.system.MsgSystem;
import io.rong.models.Result;
import io.rong.models.message.BroadcastMessage;
import io.rong.models.message.PrivateMessage;
import io.rong.models.message.SystemMessage;
import io.rong.models.response.ResponseResult;
import io.rong.models.response.TokenResult;
import io.rong.models.user.UserModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Reader;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

/**
 * IM相关操作 实现类
 */
@Slf4j
@Component
public class IMServiceImpl implements IMService {
    // 融云AppKey
    String appKey = "XXXXXXXXX";
    // 融云AppSecret
    String appSecret = "XXXXXXXX";

    @Autowired
    private RongCloudUtil utils;

    @Override
    public boolean addUser(String id, String name, String portrait) throws CusException {
        try {
            UserModel user = new UserModel(id, name, portrait);
            TokenResult result = utils.getRongCloud().user.register(user);
            if (result.code == 200) {
                return true;
            } else {
                throw new CusException("901", "同步注册im用户出错");
            }
        } catch (Exception e) {
            throw new CusException("99", "系统异常");
        }
    }

    @Override
    public boolean updateUser(String id, String name, String portrait) throws CusException {
        try {
            UserModel user = new UserModel(id, name, portrait);
            Result result = utils.getRongCloud().user.update(user);
            if (result.code == 200) {
                return true;
            } else {
                throw new CusException("902", "同步更新im用户出错");
            }
        } catch (Exception e) {
            throw new CusException("99", "系统异常");
        }
    }

    @Override
    public boolean sendPrivateMsg(String fromId, String[] targetIds, BaseMessage msg, String pushContent, String pushData) throws CusException {
        Reader reader = null;
        PrivateMessage privateMessage = new PrivateMessage()
                .setSenderId(fromId)
                .setTargetId(targetIds)
                .setObjectName(msg.getType())
                .setContent(msg)
                .setPushContent(pushContent)
                .setPushData(pushData)
                .setVerifyBlacklist(0)
                .setIsPersisted(0)
                .setIsCounted(0)
                .setIsIncludeSender(0);
        ResponseResult result = null;
        try {
            result = utils.getRongCloud().message.msgPrivate.send(privateMessage);
            if (result.code == 200) {
                return true;
            } else {
                throw new CusException("903", "发送系统消息出错");
            }
        } catch (Exception e) {
            throw new CusException("99", "系统异常");
        }
    }

    @Override
    public boolean sendSystemMax100Msg(String fromId, String[] targetIds, BaseMessage msg, String pushContent, String pushData) throws CusException {
        try {
            MsgSystem system = utils.getRongCloud().message.system;
            SystemMessage systemMessage = new SystemMessage()
                    .setSenderId(fromId)
                    .setTargetId(targetIds)
                    .setObjectName(msg.getType())
                    .setContent(msg)
                    .setPushContent(pushData)
                    .setPushData(pushData)
                    .setIsPersisted(0)
                    .setIsCounted(0)
                    .setContentAvailable(0);
            ResponseResult result = system.send(systemMessage);
            if (result.code == 200) {
                return true;
            } else {
                throw new CusException("903", "发送系统消息出错");
            }
        } catch (Exception e) {
            throw new CusException("99", "系统异常");
        }
    }

    @Override
    public boolean sendSystemBroadcastMsg(String fromId, BaseMessage msg, String pushContent, String pushData) throws CusException {
        try {
            BroadcastMessage message = new BroadcastMessage()
                    .setSenderId(fromId)
                    .setObjectName(msg.getType())
                    .setContent(msg)
                    .setPushContent(pushContent)
                    .setPushData(pushData);
            ResponseResult result = utils.getRongCloud().message.system.broadcast(message);
            if (result.code == 200) {
                return true;
            } else {
                throw new CusException("903", "发送系统消息出错");
            }
        } catch (Exception e) {
            throw new CusException("99", "系统异常");
        }
    }

    @Override
    public String getToken(String userId, String name, String portraitUri) throws CusException {
        try {
            HTTPRequest req = WS.url("http://api.cn.ronghub.com/user/getToken.json");
            Map<String, String> params = new HashMap<String, String>();
            params.put("userId", userId);
            params.put("name", name);
            params.put("portraitUri", portraitUri);
            java.util.Random r = new java.util.Random();
            String nonce = (r.nextInt(100000) + 1) + "";
            String timestamp = System.currentTimeMillis() + "";
            String signature = string2Sha1(appSecret + nonce + timestamp);
            HTTPResponse res = req.form(params).header("App-Key", appKey).header("Nonce", nonce).header("Timestamp", timestamp).header("Signature", signature).post();
            String body = res.getResponseAsString();
            JSONObject jo = JSONObject.parseObject(body);
            if (null != jo && jo.getInteger("code") == 200) {
                return jo.getString("token");
            } else {
                throw new CusException("904", "获取IM token 出现问题");
            }
        } catch (Exception e) {
            throw new CusException("99", "系统异常");
        }
    }

    @Override
    public boolean sendUserDefinedMsg(String fromId, String[] targetIds, MediaMessage msg, String pushContent, String pushData) throws CusException {
        Reader reader = null;
        PrivateMessage privateMessage = new PrivateMessage()
                .setSenderId(fromId)
                .setTargetId(targetIds)
                .setObjectName(msg.getType())
                .setContent(msg)
                .setPushContent(pushContent)
                .setPushData(pushData)
                .setCount("1")
                .setVerifyBlacklist(0)
                .setIsPersisted(0)
                .setIsCounted(0)
                .setIsIncludeSender(0);
        ResponseResult result = null;
        try {
            // 发送单聊方法
            result = utils.getRongCloud().message.msgPrivate.send(privateMessage);
            if (result.code == 200) {
                return true;
            } else {
                throw new CusException("903", "发送自定义单聊消息出错");
            }
        } catch (Exception e) {
            throw new CusException("99", "系统异常");
        }
    }

    @Override
    public Integer checkOnline(String userId) throws CusException {
        HTTPRequest req = WS.url("http://api.cn.ronghub.com/user/checkOnline.json");
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", userId);
        java.util.Random r = new java.util.Random();
        String nonce = (r.nextInt(100000) + 1) + "";
        String timestamp = System.currentTimeMillis() + "";
        String signature = string2Sha1(appSecret + nonce + timestamp);
        HTTPResponse res = req.timeout(3000).form(params).header("App-Key", appKey).header("Nonce", nonce).header("Timestamp", timestamp).header("Signature", signature).post();
        String result = res.getResponseAsString();
        Map<String, Object> resMap = JSON.parseObject(result, Map.class);
        Integer code = (Integer) resMap.get("code");
        if (code != 200) {
            log.error(userId + "调用是否在线接口结果为：" + result);
            return 2;
        }
        String status = (String) resMap.get("status");
        Integer resStatus = 0;
        if ("0".equals(status)) {
            resStatus = 0;
        } else if ("1".equals(status)) {
            resStatus = 1;
        } else {
            resStatus = 2;
        }
        return resStatus;
    }

    private static String string2Sha1(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
            mdTemp.update(str.getBytes("UTF-8"));

            byte[] md = mdTemp.digest();
            int j = md.length;
            char buf[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
                buf[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(buf);
        } catch (Exception e) {
            // TODO: handle exception
            return null;
        }
    }
}
