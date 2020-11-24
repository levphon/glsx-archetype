package com.glsx.plat.mybatis.mapper;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

public interface CommonBaseMapper<T> extends Mapper<T>, MySqlMapper<T> {

    //可以添加公共方法

    default int deleteWithVersion(T t) {
        int result = delete(t);
        if (result == 0) {
            throw new RuntimeException("删除失败!");
        }
        return result;
    }

    default int updateByPrimaryKeyWithVersion(T t) {
        int result = updateByPrimaryKey(t);
        if (result == 0) {
            throw new RuntimeException("更新失败!");
        }
        return result;
    }

}

