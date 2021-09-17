package com.glsx.plat.push.template;

import com.glsx.plat.push.constant.MessageType;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author payu
 */
public class GetuiMessage extends HashMap<String, Object> {

    public static final String MESSAGE_TYPE = "messageType";
    public static final String PACKAGE_NAME = "packageName";
    private static final long serialVersionUID = -5993986890576211345L;
    private final String TITLE = "title";
    private final String CONTENT = "content";

    /**
     * 生成推送消息
     *
     * @param typeEnum    消息类型
     * @param customParam 消息内容参数和消息点击需要的参加共享相同参数
     */
    public GetuiMessage(MessageType typeEnum, Map<String, Object> customParam) {
        assert customParam != null;
        this.putAll(customParam);
        // 把优先级高的Key放在后面，即便在customParam有重复的KEY，也不会影响。
        this.put(MESSAGE_TYPE, typeEnum.getCode());
        this.put(TITLE, typeEnum.getTitle());
        this.put(CONTENT, typeEnum.getContent(customParam));
    }

    /**
     * 生成推送消息
     *
     * @param title       消息标题
     * @param content     消息内容
     * @param customParam 消息内容参数和消息点击需要的参加共享相同参数
     */
    public GetuiMessage(String title, String content, Map<String, Object> customParam) {
        assert customParam != null;
        Assert.isTrue(!StringUtils.isEmpty(customParam.get("messageType")), "需指定消息类型messageType");

        this.putAll(customParam);
        // 把优先级高的Key放在后面，即便在customParam有重复的KEY，也不会影响。
        this.put(MESSAGE_TYPE, customParam.get("messageType"));
        this.put(this.TITLE, title);
        this.put(this.CONTENT, content);
    }

    public String getTitle() {
        return (String) this.get(TITLE);
    }

    public String getContent() {
        return (String) this.get(CONTENT);
    }
}
