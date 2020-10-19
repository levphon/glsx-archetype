package cn.com.glsx.account.modules.service;

import cn.com.glsx.account.modules.entity.Account;
import cn.com.glsx.account.modules.mapper.AccountMapper;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 账户业务逻辑处理
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AccountService {

    @Autowired
    private AccountMapper accountMapper;

    /**
     * @param accountId {@link Account#getId()}
     * @param money     扣除的金额
     */
    public void debit(Long accountId, Double money) {
        Account account = accountMapper.selectByPrimaryKey(accountId);
        if (ObjectUtils.isEmpty(account)) {
            throw new RuntimeException("账户：" + accountId + "，不存在.");
        }
        if (account.getAmount() - money < 0) {
            throw new RuntimeException("账户：" + accountId + "，余额不足.");
        }
        account.setAmount(account.getAmount() - money);
        accountMapper.updateByPrimaryKeySelective(account);
    }

    /**
     * @param userId {@link Account#getUserId()}
     * @param money  扣除的金额
     */
    public void debit(String userId, Double money) {
        Account account = accountMapper.selectByUserId(userId);
        if (ObjectUtils.isEmpty(account)) {
            throw new RuntimeException("账户：" + userId + "，不存在.");
        }
        if (account.getAmount() - money < 0) {
            throw new RuntimeException("账户：" + userId + "，余额不足.");
        }
        account.setAmount(account.getAmount() - money);
        accountMapper.updateByPrimaryKeySelective(account);
    }

}
