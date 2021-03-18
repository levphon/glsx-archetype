package cn.com.glsx.vasp.modules.mapper;

import cn.com.glsx.vasp.modules.entity.DayInfo;
import com.glsx.plat.mybatis.mapper.CommonBaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DayInfoMapper extends CommonBaseMapper<DayInfo> {
    int updateDayInsert(@Param("daysYear") List<DayInfo> daysYear);

    int getDayCodeByDate(String date);
}