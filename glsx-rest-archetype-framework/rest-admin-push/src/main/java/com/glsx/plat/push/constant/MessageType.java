package com.glsx.plat.push.constant;

import lombok.Getter;

import java.util.Map;

@Getter
public enum MessageType {

    DEFAULT("默认", "0"), SOCIAL("社交", "1"), PAY("支付", "2"), MARKET("营销", "3");

    private String title;
    private String code;

    MessageType(String title, String code) {
        this.title = title;
        this.code = code;
    }

    public String getContent(Map<String, String> customParam) {
        return null;
    }

}
