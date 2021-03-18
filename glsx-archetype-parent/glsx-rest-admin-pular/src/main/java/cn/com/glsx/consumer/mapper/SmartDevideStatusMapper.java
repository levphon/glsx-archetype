package cn.com.glsx.consumer.mapper;

import cn.com.glsx.consumer.entity.SmartDevideStatus;
import com.glsx.plat.mybatis.mapper.CommonBaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SmartDevideStatusMapper extends CommonBaseMapper<SmartDevideStatus> {
    SmartDevideStatus findDevStatusByIdAndCode(@Param("devId") String devId,@Param("code") String code);
}