package cn.com.glsx.order.modules.controller;

import cn.com.glsx.order.api.OrderFeignClient;
import cn.com.glsx.order.model.OrderBO;
import cn.com.glsx.order.modules.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author payu
 */
@RestController
@RequestMapping(value = "/order")
public class OrderFeignController implements OrderFeignClient {

    /**
     * 订单业务逻辑
     */
    @Autowired
    private OrderService orderService;

    /**
     * @param orderBO
     * @return
     */
    @Override
    @PostMapping("createOrder")
    public String submitOrder(@RequestBody OrderBO orderBO) {
        orderService.createOrder(orderBO);
        return "下单成功.";
    }

}