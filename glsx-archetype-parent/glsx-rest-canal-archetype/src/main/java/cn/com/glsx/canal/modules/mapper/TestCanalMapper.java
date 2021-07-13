package cn.com.glsx.canal.modules.mapper;

import cn.com.glsx.canal.modules.entity.TestCanal;
import com.glsx.plat.mybatis.mapper.CommonBaseMapper;
import org.apache.ibatis.annotations.Param;
import org.mapstruct.Mapper;

@Mapper
public interface TestCanalMapper extends CommonBaseMapper<TestCanal> {
    TestCanal findTestBySn(@Param("sn") String sn);

}