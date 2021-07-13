package com.glsx.plat.common.enums;

import lombok.Getter;

/**
 * @author payu
 */

@Getter
public enum OperateType {

    REGISTER("注册"),
    LOGIN("登录"), LOGOUT("登出"),
    QUERY("查询"), ADD("新增"), EDIT("编辑"), DELETE("删除"),
    IMPORT("导出"), EXPORT("导入"),
    AUDIT("审核"),
    API("API调用");

    private String type;

    OperateType(String type) {
        this.type = type;
    }

}
