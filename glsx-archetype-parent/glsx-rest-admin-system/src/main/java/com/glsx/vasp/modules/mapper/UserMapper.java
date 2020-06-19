package com.glsx.vasp.modules.mapper;

import com.glsx.plat.mybatis.mapper.CommonBaseMapper;
import com.glsx.vasp.modules.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends CommonBaseMapper<User> {

    User selectByPhone(String phone);

    void logicDeleteById(Long id);

}