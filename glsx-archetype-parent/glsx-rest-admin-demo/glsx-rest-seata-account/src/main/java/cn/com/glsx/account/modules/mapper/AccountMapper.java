package cn.com.glsx.account.modules.mapper;

import cn.com.glsx.account.modules.entity.Account;
import com.glsx.plat.mybatis.mapper.CommonBaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountMapper extends CommonBaseMapper<Account> {

    Account selectByUserId(String userId);

}