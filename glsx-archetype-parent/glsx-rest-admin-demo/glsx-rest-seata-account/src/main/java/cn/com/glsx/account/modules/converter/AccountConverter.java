package cn.com.glsx.account.modules.converter;

import cn.com.glsx.account.model.AccountBO;
import cn.com.glsx.account.model.AccountDTO;
import cn.com.glsx.account.modules.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 解耦dao与api的实体映射
 * 字段名不一样，用@Mapping来处理
 * https://github.com/mapstruct/mapstruct-examples
 *
 * @author payu
 */
@Mapper
public interface AccountConverter {

    AccountConverter INSTANCE = Mappers.getMapper(AccountConverter.class);

    AccountDTO do2dto(Account account);

    Account dto2do(AccountDTO accountDTO);

    AccountBO do2bo(Account account);

    Account bo2do(AccountBO accountBO);

}
