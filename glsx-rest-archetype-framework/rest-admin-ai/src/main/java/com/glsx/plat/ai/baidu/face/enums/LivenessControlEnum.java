package com.glsx.plat.ai.baidu.face.enums;

import lombok.Getter;

@Getter
public enum LivenessControlEnum {

    NONE("");

    private String name;

    LivenessControlEnum(String name) {
        this.name = name;
    }

}
