package cn.com.glsx.vasp.modules.mapper;

import cn.com.glsx.vasp.modules.entity.DeviceStatus;
import com.glsx.plat.mybatis.mapper.CommonBaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DeviceStatusMapper extends CommonBaseMapper<DeviceStatus> {
    DeviceStatus getByIdAndCode(@Param("deviceId") String deviceId, @Param("deviceOrderCode")String deviceOrderCode);
}