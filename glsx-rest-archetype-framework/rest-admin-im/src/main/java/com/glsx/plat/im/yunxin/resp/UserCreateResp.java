package com.glsx.plat.im.yunxin.resp;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserCreateResp extends AbstractYunxinResponse {

    @JSONField(name = "info")
    private UserCreateRespInfo info;

}
