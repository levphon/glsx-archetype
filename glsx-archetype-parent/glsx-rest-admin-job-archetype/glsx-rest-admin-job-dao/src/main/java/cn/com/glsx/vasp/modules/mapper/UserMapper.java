package cn.com.glsx.vasp.modules.mapper;

import cn.com.glsx.vasp.modules.model.UserSearch;
import com.glsx.plat.mybatis.mapper.CommonBaseMapper;
import cn.com.glsx.vasp.modules.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper extends CommonBaseMapper<User> {

    List<User> selectList(UserSearch search);

    User selectByPhone(String phone);

    void logicDeleteById(Long id);

}