package cn.com.glsx.order.modules.mapper;

import cn.com.glsx.order.modules.entity.Order;
import com.glsx.plat.mybatis.mapper.CommonBaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends CommonBaseMapper<Order> {
}