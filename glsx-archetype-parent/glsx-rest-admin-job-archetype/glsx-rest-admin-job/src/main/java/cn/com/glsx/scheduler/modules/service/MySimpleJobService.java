package cn.com.glsx.scheduler.modules.service;

import cn.com.glsx.admin.modules.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author liuyufeng
 * @date 2021年01月26日 下午2:37:40
 */
@Service
public class MySimpleJobService {

    @Autowired
    private UserMapper userMapper;

    public void simpleJob(String name) {
        System.out.println("呼叫：" + name);
    }

}
