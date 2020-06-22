package com.glsx.plat.wechat.modules.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import com.glsx.plat.common.annotation.SysLog;
import com.glsx.plat.core.web.R;
import com.glsx.plat.wechat.common.config.WxMaConfiguration;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.lang3.StringUtils;
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
public class WxMaUserController {

    /**
     * 登陆接口
     *
     * @param appid
     * @param js_code
     * @return
     */
    @SysLog
    @PostMapping(value = "/login", produces = "application/json")
    public R login(@PathVariable String appid, @RequestParam("js_code") String js_code) throws WxErrorException {
        if (StringUtils.isBlank(js_code)) return R.error("empty jscode");

        final WxMaService wxMaService = WxMaConfiguration.getMaService(appid);

        WxMaJscode2SessionResult session = wxMaService.getUserService().getSessionInfo(js_code);
        log.info(session.toString());
        //可以增加自己的逻辑，关联业务相关数据
        //todo openid入库
        //todo 设置session
        //todo ...
        Map<String, Object> rtnMap = cacheUser(session);

        return R.ok().data(rtnMap);
    }

    /**
     * 关联登录用户到数据库、服务器端缓存用户信息等
     *
     * @param session
     */
    protected Map<String, Object> cacheUser(WxMaJscode2SessionResult session) {
        return null;
    }

    /**
     * 登陆接口
     *
     * @param appid
     * @param js_code
     * @return
     */
    @SysLog
    @PostMapping(value = "/loginByOpenid", produces = "application/json")
    public R loginByOpenid(@PathVariable String appid, @RequestParam("js_code") String js_code) throws WxErrorException {
        if (StringUtils.isBlank(js_code)) return R.error("empty jscode");

        final WxMaService wxMaService = WxMaConfiguration.getMaService(appid);

        WxMaJscode2SessionResult session = wxMaService.getUserService().getSessionInfo(js_code);
        log.info(session.toString());
        //登录
        Map<String, Object> rtnMap = loginByOpenid(session);
        return R.ok().data(rtnMap);
    }

    /**
     * openid免登录
     *
     * @param session
     */
    protected Map<String, Object> loginByOpenid(WxMaJscode2SessionResult session) {
        return null;
    }

    /**
     * <pre>
     * 获取用户信息接口
     * </pre>
     */
    @GetMapping("/info")
    public R info(@PathVariable String appid,
                  String signature, String rawData, String encryptedData, String iv) {
        final WxMaService wxMaService = WxMaConfiguration.getMaService(appid);

        String sessionKey = getSessionKeyFromCache();

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
    protected Map<String, Object> linkUser(WxMaUserInfo userInfo) {
        return null;
    }

    /**
     * 从jwt中获取sessionKey（或redis）
     *
     * @return
     */
    protected String getSessionKeyFromCache() {
        return "";
    }

    /**
     * <pre>
     * 获取用户绑定手机号信息
     * </pre>
     */
    @SysLog
    @GetMapping("/phone")
    public R phone(@PathVariable String appid, String signature, String rawData, String encryptedData, String iv) {
        final WxMaService wxMaService = WxMaConfiguration.getMaService(appid);

        String sessionKey = getSessionKeyFromCache();

        //用户信息校验
        if (!wxMaService.getUserService().checkUserInfo(sessionKey, rawData, signature)) {
            return R.error("user check failed");
        }
        // 解密
        WxMaPhoneNumberInfo phoneNoInfo = wxMaService.getUserService().getPhoneNoInfo(sessionKey, encryptedData, iv);

        Map<String, Object> rtnMap = updatePhone(phoneNoInfo);

        return R.ok().data(rtnMap.get("token"));
    }

    /**
     * 关联登录用户到数据库、服务器端缓存用户信息等
     *
     * @param phoneNoInfo
     */
    protected Map<String, Object> updatePhone(WxMaPhoneNumberInfo phoneNoInfo) {
        return null;
    }

    /**
     * <pre>
     * 获取用户绑定手机号信息
     * </pre>
     */
    @PostMapping("/updateWxUserInfo")
    public R update(@PathVariable String appid, @RequestBody WxMaUserInfo userInfo) {
        return R.ok();
    }

}
