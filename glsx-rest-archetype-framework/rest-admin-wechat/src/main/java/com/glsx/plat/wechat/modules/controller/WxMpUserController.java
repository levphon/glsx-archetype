package com.glsx.plat.wechat.modules.controller;

import com.glsx.plat.common.annotation.NoLogin;
import com.glsx.plat.common.annotation.SysLog;
import com.glsx.plat.core.web.R;
import com.glsx.plat.exception.SystemMessage;
import lombok.AllArgsConstructor;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/wx/mp/")
public abstract class WxMpUserController {

    private final WxMpService wxMpService;

    @NoLogin
    @RequestMapping("/getToken")
    public R getToken() throws WxErrorException {
        String token = wxMpService.getAccessToken();
        return R.ok().data(token);
    }

    @NoLogin
    @RequestMapping("/getTicket")
    public R getTicket() throws WxErrorException {
        String ticket = wxMpService.getJsapiTicket();
        return R.ok().data(ticket);
    }

    @NoLogin
    @RequestMapping("/getOpenid")
    public R getOpenid(@RequestParam String code) throws WxErrorException {
        WxOAuth2AccessToken accessToken = wxMpService.getOAuth2Service().getAccessToken(code);
        return R.ok().data(accessToken.getOpenId());
    }

    @RequestMapping("/getUser")
    public R getUser(@RequestParam String code) throws WxErrorException {
        WxOAuth2AccessToken accessToken = wxMpService.getOAuth2Service().getAccessToken(code);

        WxMpUser user = wxMpService.getUserService().userInfo(accessToken.getOpenId());

        Map<String, Object> rtnMap = linkUser(user);

        return R.ok().data(rtnMap);
    }

    /**
     * 关联登录用户到数据库、服务器端缓存用户信息等
     *
     * @param user
     */
    protected abstract Map<String, Object> linkUser(WxMpUser user);

    /**
     * 登陆接口
     *
     * @param code
     * @return
     */
    @SysLog
    @NoLogin
    @RequestMapping(value = "/loginByOpenid")
    public R loginByOpenid(@RequestParam String code) throws WxErrorException {
        WxOAuth2AccessToken accessToken = wxMpService.getOAuth2Service().getAccessToken(code);

        Map<String, Object> rtnMap = loginByOpenid(accessToken);
        if (CollectionUtils.isEmpty(rtnMap)) {
            return R.error(SystemMessage.NO_LOGIN_INVALID.getCode(), SystemMessage.NO_LOGIN_INVALID.getMsg()).data(accessToken.getOpenId());
        }
        return R.ok().data(rtnMap);
    }

    /**
     * openid免登录
     *
     * @param accessToken
     */
    protected abstract Map<String, Object> loginByOpenid(WxOAuth2AccessToken accessToken);

}
