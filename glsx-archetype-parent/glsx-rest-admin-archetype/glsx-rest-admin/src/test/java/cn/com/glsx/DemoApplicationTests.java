package cn.com.glsx;

import cn.com.glsx.admin.modules.user.service.UserService;
import com.glsx.biz.common.base.entity.Carbrand;
import com.glsx.biz.common.base.service.CarbrandService;
import com.glsx.biz.merchant.common.entity.Merchant;
import com.glsx.biz.merchant.service.MerchantService;
import com.glsx.biz.user.common.entity.User;
import com.glsx.cloudframework.exception.ServiceException;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DemoApplicationTests {

//    @Autowired
//    private UserService userService;

    @DubboReference(version = "1.0.0")
    private MerchantService merchantService;

    @DubboReference(version = "2.2.0")
    private CarbrandService carbrandService;

    @DubboReference(version = "1.0.0")
    private com.glsx.biz.user.service.UserService userService;

    @Test
    public void testLogin() {
//        userService.findByPhone("13800138000");
    }

    @Test
    public void getMerchantById() {
        Merchant merchant = merchantService.get(44184571);
        System.out.println(merchant);
    }

    @Test
    public void getAllCarbrand() {
        List<Carbrand> carbrandList = carbrandService.getAllList();
        System.out.println(carbrandList.size());
    }

    @Test
    public void testUser() throws ServiceException {
        User user = userService.getByUserId(92001768);
        System.out.println(user.getAccountId());
    }
}