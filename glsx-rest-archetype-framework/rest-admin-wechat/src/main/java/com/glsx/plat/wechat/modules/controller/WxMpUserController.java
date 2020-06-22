package com.glsx.plat.wechat.modules.controller;

import com.glsx.plat.core.web.R;
import lombok.AllArgsConstructor;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/wx/mp/{appid}")
public class WxMpUserController {

    private final WxMpService wxService;

    @RequestMapping("/getToken")
    public R getToken(@PathVariable String appid) throws WxErrorException {
        if (!this.wxService.switchover(appid)) {
            throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appid));
        }

        String token = wxService.getAccessToken();
        return R.ok().data(token);
    }

    @RequestMapping("/getTicket")
    public R getTicket(@PathVariable String appid) throws WxErrorException {
        if (!this.wxService.switchover(appid)) {
            throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appid));
        }

        String ticket = wxService.getJsapiTicket();
        return R.ok().data(ticket);
    }

    @RequestMapping("/getOpenid")
    public R getOpenid(@PathVariable String appid, @RequestParam String code) throws WxErrorException {
        if (!this.wxService.switchover(appid)) {
            throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appid));
        }

        WxMpOAuth2AccessToken accessToken = wxService.oauth2getAccessToken(code);
        return R.ok().data(accessToken.getOpenId());
    }

    @RequestMapping("/getUser")
    public R getUser(@PathVariable String appid, @RequestParam String code) throws WxErrorException {
        if (!this.wxService.switchover(appid)) {
            throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appid));
        }

        WxMpOAuth2AccessToken accessToken = wxService.oauth2getAccessToken(code);
        WxMpUser user = wxService.oauth2getUserInfo(accessToken, null);
        return R.ok().data(user);
    }

}
