package com.glsx.plat.wechat.modules.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.springframework.stereotype.Service;

/**
 * @author payu
 */
@Slf4j
@Service
@AllArgsConstructor
public class WxPushService {

    private final WxMpService wxService;

    public void push(String appid, WxMpTemplateMessage message) {
        if (!this.wxService.switchover(appid)) {
            throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appid));
        }
        try {
            wxService.getTemplateMsgService().sendTemplateMsg(message);
        } catch (Exception e) {
            log.error("推送失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

}
