package com.glsx.plat.wechat.modules.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import com.glsx.plat.common.annotation.NoLogin;
import com.glsx.plat.common.annotation.SysLog;
import com.glsx.plat.core.web.R;
import com.glsx.plat.wechat.config.WxMaConfiguration;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 微信小程序用户接口
 *
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 */
@Slf4j
@RestController
@RequestMapping("/wx/user/{appid}")
public abstract class WxMaUserController {

    /**
     * 登陆接口
     *
     * @param appid
     * @param code
     * @return
     */
    @SysLog
    @NoLogin
    @ApiOperation("登录ByCode")
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public R login(@PathVariable String appid, @RequestParam("code") String code) throws WxErrorException {
        if (StringUtils.isBlank(code)) return R.error("empty jscode");

        final WxMaService wxMaService = WxMaConfiguration.getMaService(appid);
        WxMaJscode2SessionResult session = wxMaService.getUserService().getSessionInfo(code);
        log.info(session.toString());

        //增加自己的逻辑，关联业务相关数据
        //todo openid入库
        //todo 设置session
        Map<String, Object> rtnMap = cacheUser(session);

        return R.ok().data(rtnMap);
    }

    /**
     * 关联登录用户到数据库、服务器端缓存用户信息等
     *
     * @param session
     */
    protected abstract Map<String, Object> cacheUser(WxMaJscode2SessionResult session);

    /**
     * <pre>
     * 获取用户信息接口
     * </pre>
     */
    @ApiOperation("获取用户信息")
    @GetMapping("/info")
    public R info(@PathVariable String appid,
                  String signature, String rawData, String encryptedData, String iv) {

        String sessionKey = getSessionKeyFromCache();

        final WxMaService wxMaService = WxMaConfiguration.getMaService(appid);
        // 用户信息校验
        if (!wxMaService.getUserService().checkUserInfo(sessionKey, rawData, signature)) {
            return R.error("user check failed");
        }

        // 解密用户信息
        WxMaUserInfo userInfo = wxMaService.getUserService().getUserInfo(sessionKey, encryptedData, iv);

        linkUser(userInfo);

        return R.ok().data(userInfo);
    }

    /**
     * 关联登录用户到数据库、服务器端缓存用户信息等
     *
     * @param userInfo
     */
    protected abstract Map<String, Object> linkUser(WxMaUserInfo userInfo);

    /**
     * 从jwt中获取sessionKey（或redis）
     *
     * @return
     */
    protected abstract String getSessionKeyFromCache();

    /**
     * <pre>
     * 获取用户绑定手机号信息
     * </pre>
     */
    @ApiOperation("获取用户手机号码")
    @SysLog
    @GetMapping("/phone")
    public R phone(@PathVariable String appid, String signature, String rawData, String encryptedData, String iv) {

        String sessionKey = getSessionKeyFromCache();

        final WxMaService wxMaService = WxMaConfiguration.getMaService(appid);
        //用户信息校验
        if (!wxMaService.getUserService().checkUserInfo(sessionKey, rawData, signature)) {
            return R.error("user check failed");
        }
        // 解密
        WxMaPhoneNumberInfo phoneNoInfo = wxMaService.getUserService().getPhoneNoInfo(sessionKey, encryptedData, iv);

        Map<String, Object> rtnMap = linkPhone(phoneNoInfo);

        return R.ok().data(rtnMap.get("token"));
    }

    /**
     * 关联登录用户到数据库、服务器端缓存用户信息等
     *
     * @param phoneNoInfo
     */
    protected abstract Map<String, Object> linkPhone(WxMaPhoneNumberInfo phoneNoInfo);

}
