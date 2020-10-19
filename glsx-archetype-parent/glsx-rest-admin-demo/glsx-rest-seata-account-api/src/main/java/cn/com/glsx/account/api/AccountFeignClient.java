package cn.com.glsx.account.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author payu
 */
@FeignClient(name = "glsx-rest-seata-account", path = "/account")
public interface AccountFeignClient {
    
    /**
     * 扣除指定账户金额
     *
     * @param userId 账户编号
     * @param money  金额
     */
    @PostMapping("/deductByUserId")
    void deduct(@RequestParam("userId") String userId, @RequestParam("money") Double money);

}