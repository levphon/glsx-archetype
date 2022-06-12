package com.glsx.plat.wechat.modules.service;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.binarywang.spring.starter.wxjava.miniapp.properties.WxMaProperties;
import com.glsx.plat.wechat.config.WxMaConfiguration;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
@AllArgsConstructor
public class WxUserService {

    @Resource
    private WxMaProperties wxMaProperties;

    public WxMaJscode2SessionResult getSessionInfo(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        final WxMaService wxMaService = WxMaConfiguration.getMaService(wxMaProperties.getAppid());
        WxMaJscode2SessionResult session = null;
        try {
            session = wxMaService.getUserService().getSessionInfo(code);
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
        return session;
    }

}
