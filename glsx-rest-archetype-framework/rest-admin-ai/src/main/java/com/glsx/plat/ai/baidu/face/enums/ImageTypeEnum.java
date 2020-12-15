package com.glsx.plat.ai.baidu.face.enums;

import lombok.Getter;

@Getter
public enum ImageTypeEnum {

    URL("url"), BASE64("base64");

    private String name;

    ImageTypeEnum(String name) {
        this.name = name;
    }

}
