package cn.com.glsx.task.modules.service;

import org.springframework.stereotype.Service;

/**
 * @author liuyf
 * @date 2021年01月26日 下午2:37:40
 */
@Service
public class MySimpleJobService {

    public void simpleJob(String name) {
        System.out.println("呼叫：" + name);
    }

}
