package com.glsx.vasp.modules.mapper;

import com.glsx.plat.mybatis.mapper.CommonBaseMapper;
import com.glsx.vasp.modules.entity.User;
import com.glsx.vasp.modules.model.UserSearch;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper extends CommonBaseMapper<User> {

    List<User> selectList(UserSearch search);

    User selectByPhone(String phone);

    void logicDeleteById(Long id);

}