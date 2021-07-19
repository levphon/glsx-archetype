package com.glsx.plat.im.rongcloud.message;

import io.rong.messages.BaseMessage;
import io.rong.util.GsonUtil;
import lombok.Data;

@Data
public class MediaMessage extends BaseMessage {

    // 自定义消息标志
    private static final transient String TYPE = "go:media";

    private String content = "";

    // 以下是自定义参数
    private String targetId;
    private String sendId;
    private Long sendTime;
    private Long receiveTime;
    private String userAge;
    private String userCount;

    public MediaMessage() {
    }

    public MediaMessage(String content) {
        this.content = content;
    }

    @Override
    public String getType() {
        return "rx:media";
    }

    @Override
    public String toString() {
        return GsonUtil.toJson(this, MediaMessage.class);
    }

}