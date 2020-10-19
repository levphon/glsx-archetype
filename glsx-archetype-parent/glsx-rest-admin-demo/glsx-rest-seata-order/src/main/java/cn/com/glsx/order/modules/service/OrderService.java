package cn.com.glsx.order.modules.service;

import cn.com.glsx.account.api.AccountFeignClient;
import cn.com.glsx.order.model.OrderBO;
import cn.com.glsx.order.modules.converter.OrderConverter;
import cn.com.glsx.order.modules.entity.Order;
import cn.com.glsx.order.modules.mapper.OrderMapper;
import com.glsx.plat.common.utils.SnowFlake;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author payu
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class OrderService {

    /**
     * 账户服务接口
     */
    @Autowired
    private AccountFeignClient accountFeignClient;

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 创建订单
     * 通过{@link AccountFeignClient#deduct(String, Double)}方法扣除商品所需要的金额，金额不足由account-service抛出异常
     *
     * @param orderBO
     */
    @GlobalTransactional
    public void createOrder(OrderBO orderBO) {
        log.info("开始全局事务，XID = " + RootContext.getXID());
        Double orderMoney = calculate(orderBO.getCommodityCode(), orderBO.getOrderCount());

        accountFeignClient.deduct(orderBO.getUserId(), orderMoney);

        Order order = OrderConverter.INSTANCE.bo2do(orderBO);
        //生成订单号
        order.setOrderNo(SnowFlake.nextSerialNumber());
        order.setAmount(orderMoney);
        orderMapper.insert(order);
    }

    private Double calculate(String commodityCode, int orderCount) {
        return 100.00;
    }

}