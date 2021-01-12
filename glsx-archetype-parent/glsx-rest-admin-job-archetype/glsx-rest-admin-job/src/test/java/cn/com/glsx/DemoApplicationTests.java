package cn.com.glsx;

import cn.com.glsx.task.modules.service.MySimpleJobService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DemoApplicationTests {

    @Autowired
    private MySimpleJobService mySimpleJobService;

    @Test
    public void testLogin() {
        mySimpleJobService.simpleJob("13800138000");
    }

}