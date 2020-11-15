package com.glsx.plat.im.yunxin.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateReq extends AbstractYunxinRequest {

    /**
     * 网易云通信ID,APP内唯一
     */
    private String accid;

}
