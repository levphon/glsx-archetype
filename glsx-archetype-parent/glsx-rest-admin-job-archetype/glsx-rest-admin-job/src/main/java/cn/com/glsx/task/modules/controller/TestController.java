package cn.com.glsx.task.modules.controller;

import cn.com.glsx.task.config.ElasticJobConfig;
import cn.com.glsx.task.taskscheduler.MySimpleJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private ElasticJobConfig elasticJobConfig;

    @RequestMapping("/addJob")
    public void addJob() {
        int shardingTotalCount = 2;
        elasticJobConfig.addSimpleJobScheduler(MySimpleJob.class, "*/10 * * * * ?", shardingTotalCount, "0=A,1=B");
    }

}
