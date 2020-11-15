package com.glsx.plat.im.yunxin.resp;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

@Data
public abstract class AbstractYunxinResponse implements Serializable {

    @JSONField(name = "code")
    private Integer code;

}
