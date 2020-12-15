package com.glsx.plat.ai.baidu.face.enums;

import lombok.Getter;

@Getter
public enum ActionTypeEnum {

    REPLACE("");

    private String name;

    ActionTypeEnum(String name) {
        this.name = name;
    }

}
