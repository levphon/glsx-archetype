package cn.com.glsx.order.api;

import cn.com.glsx.order.model.OrderBO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author payu
 */
@FeignClient(name = "glsx-rest-seata-order", path = "/order")
public interface OrderFeignClient {

    @PostMapping("/submitOrder")
    String submitOrder(@RequestBody OrderBO orderBO);

}
