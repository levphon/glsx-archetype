package cn.com.glsx.order.modules.converter;

import cn.com.glsx.order.model.OrderBO;
import cn.com.glsx.order.model.OrderDTO;
import cn.com.glsx.order.modules.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * 解耦dao与api的实体映射
 * 字段名不一样，用@Mapping来处理
 * https://github.com/mapstruct/mapstruct-examples
 *
 * @author payu
 */
@Mapper
public interface OrderConverter {

    OrderConverter INSTANCE = Mappers.getMapper(OrderConverter.class);

    @Mappings({
            @Mapping(source = "count", target = "orderCount"),
            @Mapping(source = "amount", target = "orderAmount")
    })
    OrderDTO do2dto(Order order);

    @Mappings({
            @Mapping(source = "orderCount", target = "count"),
            @Mapping(source = "orderAmount", target = "amount")
    })
    Order dto2do(OrderDTO orderDTO);

    @Mappings({
            @Mapping(source = "count", target = "orderCount"),
    })
    OrderBO do2bo(Order order);

    @Mappings({
            @Mapping(source = "orderCount", target = "count"),
    })
    Order bo2do(OrderBO orderBO);

}
