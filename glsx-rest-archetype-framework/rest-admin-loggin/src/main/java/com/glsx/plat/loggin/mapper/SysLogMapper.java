package com.glsx.plat.loggin.mapper;

import com.glsx.plat.loggin.entity.SysLogEntity;
import com.glsx.plat.loggin.model.LogginSearch;
import com.glsx.plat.mybatis.mapper.CommonBaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysLogMapper extends CommonBaseMapper<SysLogEntity> {

    List<SysLogEntity> search(LogginSearch search);

}