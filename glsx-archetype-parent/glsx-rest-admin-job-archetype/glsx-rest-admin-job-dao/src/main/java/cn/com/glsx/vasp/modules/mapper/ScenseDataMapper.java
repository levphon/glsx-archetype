package cn.com.glsx.vasp.modules.mapper;

import cn.com.glsx.vasp.modules.entity.ScenseData;
import com.glsx.plat.mybatis.mapper.CommonBaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ScenseDataMapper extends CommonBaseMapper<ScenseData> {
    List<ScenseData> getScenseAnyCondition(int executeCycle);

    List<ScenseData> getScenseAllCondition(int executeCycle);
}