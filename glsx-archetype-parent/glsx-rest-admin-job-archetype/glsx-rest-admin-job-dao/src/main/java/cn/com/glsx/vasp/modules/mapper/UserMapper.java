package cn.com.glsx.admin.modules.mapper;

import cn.com.glsx.admin.modules.entity.User;
import cn.com.glsx.admin.modules.model.UserSearch;
import com.glsx.plat.mybatis.mapper.CommonBaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper extends CommonBaseMapper<User> {

    List<User> selectList(UserSearch search);

    User selectByPhone(String phone);

    void logicDeleteById(Long id);

}