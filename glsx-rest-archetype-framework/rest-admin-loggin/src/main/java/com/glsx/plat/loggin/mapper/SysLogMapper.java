package com.glsx.plat.loggin.mapper;

import com.glsx.plat.loggin.entity.SysLogEntity;
import com.glsx.plat.loggin.model.LogginSearch;
import com.glsx.plat.mybatis.mapper.CommonBaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysLogMapper extends CommonBaseMapper<SysLogEntity> {

    List<SysLogEntity> search(LogginSearch search);

    int updateByTraceId(@Param("traceId") String traceId, @Param("result") String result);

}