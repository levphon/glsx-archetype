package cn.com.glsx.account.modules.controller;

import cn.com.glsx.account.api.AccountFeignClient;
import cn.com.glsx.account.modules.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 账户接口实现
 *
 * @author payu
 */
@RestController
@RequestMapping("/account")
public class AccountFeignController implements AccountFeignClient {

    /**
     * 账户业务逻辑
     */
    @Autowired
    private AccountService accountService;

    @Override
    @PostMapping(value = "/deductByUserId", consumes = "application/x-www-form-urlencoded")
    public void deduct(@RequestParam("userId") String userId, @RequestParam("money") Double money) {
        accountService.debit(userId, money);
    }

}