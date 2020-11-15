package com.glsx.plat.im.yunxin.service;

import com.glsx.plat.im.yunxin.YunxinConfig;
import com.glsx.plat.im.yunxin.req.UserCreateReq;
import com.glsx.plat.im.yunxin.resp.UserCreateResp;
import com.glsx.plat.im.yunxin.resp.UserCreateRespInfo;
import com.glsx.plat.im.yunxin.util.YunxinUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class YunxinService {

    @Autowired
    private YunxinConfig config;

    @Autowired
    private YunxinUtils utils;

    public UserCreateResp userCreate(UserCreateReq req) {
        UserCreateResp resp = utils.access(config.getUserCreateUrl(), req, UserCreateResp.class);
        return resp;
    }

    public UserCreateRespInfo userCreate(String accid) {
        UserCreateResp resp = utils.access(config.getUserCreateUrl(), new UserCreateReq(accid), UserCreateResp.class);
        if (!Objects.isNull(resp) && 200 == resp.getCode()) {
            return resp.getInfo();
        }
        return null;
    }

}
