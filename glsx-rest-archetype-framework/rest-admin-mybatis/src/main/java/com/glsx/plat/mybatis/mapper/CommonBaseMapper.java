package com.glsx.plat.mybatis.mapper;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

public interface CommonBaseMapper<T> extends Mapper<T>, MySqlMapper<T> {

    //可以添加公共方法

}

