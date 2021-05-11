package cn.com.glsx.consumer;

import cn.com.glsx.consumer.service.TuyaConmsumerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author zhouhaibao
 * @date 2021/3/15 17:46
 */
@Controller
@RequestMapping("/test")
@Slf4j
public class TestController {

    @Autowired
    private TuyaConmsumerService tuyaConmsumerService;

    @RequestMapping("/test")
    @ResponseBody
    public void test() {
        String data="{\"dataId\":\"AAW9APQog9AQo9A81QGYDUAAB\",\"devId\":\"6ce23622e4f6f960c5jhle\",\"productKey\":\"xruwvmir2e41r9db\",\"status\":[{\"code\":\"basic_flip\",\"103\":\"false\",\"t\":1615444744140,\"value\":false}]}";
        tuyaConmsumerService.processMessage(data);
    }
}
