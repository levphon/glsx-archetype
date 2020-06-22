package com.glsx.plat.wechat.modules.controller;

import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.BaseWxPayResult;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.glsx.plat.common.annotation.NoLogin;
import com.glsx.plat.common.annotation.SysLog;
import com.glsx.plat.common.utils.SnowFlake;
import com.glsx.plat.core.web.R;
import com.glsx.plat.web.utils.IpUtils;
import com.glsx.plat.wechat.modules.model.UnifiedOrderModel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author liuyf
 * @ClassName: WxPayController
 * @Description:
 * @date 2019年10月23日
 */
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/wx/pay/{appid}")
public class WxPayController {

    @Resource
    private HttpServletRequest request;

    @Resource
    private WxPayService wxPayService;

//    @Resource
//    private TradingService tradingService;

    private final WxMpService wxService;

    @RequestMapping("/config")
    public R wxJsSdkConfig(@PathVariable String appid, String url) throws WxErrorException {
        if (!this.wxService.switchover(appid)) {
            throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appid));
        }
        WxJsapiSignature wxJsapiSignature = wxService.createJsapiSignature(url);
        return R.ok().data(wxJsapiSignature);
    }

    @SysLog
    @PostMapping("unifiedOrder")
    public R unifiedOrder(UnifiedOrderModel model) throws WxPayException {
//        Customer user = getSessionUser();
//        if (StringUtils.isNullOrEmpty(user.getWxOpenId()))
//            throw BusinessException.create(ResultCodeEnum.USER_NOT_FOLLOW_OFFICIAL_ACCOUNT.getCode(), ResultCodeEnum.USER_NOT_FOLLOW_OFFICIAL_ACCOUNT.getMsg());

        if (model.getTotalFee() == null || model.getTotalFee() <= 0)
            throw WxPayException.newBuilder().build();//.create("支付金额有误或此订单无需支付");

        String notifyUrl = "";//PropertiesUtils.getProperty("wechat.repayment.notifyUrl");
        log.info("支付通知url：" + notifyUrl);
        model.setNotifyUrl(notifyUrl);

        String tradeFlowNo = "B" + SnowFlake.nextSerialNumber();
        String openid = "";
        WxPayMpOrderResult result = unifiedOrder(openid, tradeFlowNo, model);

        //交易记录入库
//        TradingFlow tradingFlow = new TradingFlow();
//        tradingFlow.setFlowNo(tradeFlowNo);
//        tradingFlow.setOrderId(model.getOrderId());
//        tradingFlow.setBody(model.getBody());
//        tradingFlow.setType(0);
//        tradingFlow.setStatus(0);
//        tradingFlow.setCreateTime(new Date());
//        tradingService.save(tradingFlow);

        return R.ok().data(result);
    }

    /**
     * 统一订单
     *
     * @param openid
     * @param tradeFlowNo
     * @param model
     * @return
     * @throws WxPayException
     */
    private WxPayMpOrderResult unifiedOrder(String openid, String tradeFlowNo, UnifiedOrderModel model) throws WxPayException {
        final WxPayUnifiedOrderRequest unifiedOrderRequest = WxPayUnifiedOrderRequest.newBuilder()
                .body(model.getBody())
                .totalFee(model.getTotalFee())
                .spbillCreateIp(IpUtils.getIpAddr(request))
                .notifyUrl(model.getNotifyUrl())
                .tradeType(WxPayConstants.TradeType.JSAPI)
                .openid(openid)
                .outTradeNo(tradeFlowNo)
                .limitPay(WxPayConstants.LimitPay.NO_CREDIT)
                .build();
        unifiedOrderRequest.setSignType(WxPayConstants.SignType.MD5);
        return this.wxPayService.createOrder(unifiedOrderRequest);
    }

    /**
     * 微信支付回调接口
     *
     * @return
     */
    @SysLog
    @NoLogin
    @RequestMapping(value = "callBackWxPay", produces = {"application/xml; charset=UTF-8"})
    @ResponseBody
    public String callBackWxPay(@RequestBody String xmlData) {
        log.info("================================================开始处理微信支付发送的异步通知");
        try {
            final WxPayOrderNotifyResult result = this.wxPayService.parseOrderNotifyResult(xmlData);
            //这里是存储我们发起支付时订单号的信息，取出来
            String outTradeNo = result.getOutTradeNo();
            String payId = result.getTransactionId();
            //分转化成元
            String totalFee = BaseWxPayResult.fenToYuan(result.getTotalFee());

            /**
             * todo 系统内部业务，修改订单状态之类的
             */

            //成功后回调微信信息
            return WxPayNotifyResponse.success("回调成功！");
        } catch (WxPayException e) {
            e.printStackTrace();
            return WxPayNotifyResponse.fail("回调有误!");
        }
    }

}
