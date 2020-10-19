package cn.com.glsx.business.modules.service;

import cn.com.glsx.order.api.OrderFeignClient;
import cn.com.glsx.order.model.OrderBO;
import cn.com.glsx.storage.api.StorageFeignClient;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class BusinessService {

    @Resource
    private StorageFeignClient storageFeignClient;

    @Resource
    private OrderFeignClient orderFeignClient;

    /**
     * 减库存，下订单
     * 通过{@link StorageFeignClient#reduceStock(String, int)}方法减少商品的库存，判断库存剩余数量
     *
     * @param userId
     * @param commodityCode
     * @param orderCount
     */
    @GlobalTransactional
    public void purchase(String userId, String commodityCode, int orderCount) {
        log.info("开始全局事务，XID = " + RootContext.getXID());

        //1、扣减库存
        storageFeignClient.reduceStock(commodityCode, orderCount);

        //2、创建订单
        OrderBO orderBO = new OrderBO();
        orderBO.setUserId(userId);
        orderBO.setCommodityCode(commodityCode);
        orderBO.setOrderCount(orderCount);
        orderFeignClient.submitOrder(orderBO);
    }

}
