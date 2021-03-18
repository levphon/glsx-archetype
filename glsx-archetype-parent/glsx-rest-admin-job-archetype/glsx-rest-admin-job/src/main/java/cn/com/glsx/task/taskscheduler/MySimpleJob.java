package cn.com.glsx.task.taskscheduler;

import cn.com.glsx.task.modules.service.MySimpleJobService;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.elasticjob.lite.annotation.ElasticSimpleJob;
import com.glsx.plat.common.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Slf4j
@Component
@ElasticSimpleJob(jobName = "MySimpleJob",cron="0/10 * * * * ?")
public class MySimpleJob implements SimpleJob {

    @Autowired
    private MySimpleJobService simpleJobService;

    @Override
    public void execute(ShardingContext shardingContext) {
        log.info("Thread ID: {}, 作业分片总数: {}, " +
                        "当前分片项: {}.当前参数: {}," +
                        "作业名称: {}.作业自定义参数: {}",
                Thread.currentThread().getId(),
                shardingContext.getShardingTotalCount(),
                shardingContext.getShardingItem(),
                shardingContext.getShardingParameter(),
                shardingContext.getJobName(),
                shardingContext.getJobParameter()
        );
        List<Integer> list =new ArrayList<>();
        String normalDate = DateUtils.formatNormal(new Date());
        log.info("---------------{}-开始----------------", normalDate);
        simpleJobService.simpleJob("张三 " + normalDate);
        log.info("---------------{}-结束--------------", normalDate);
    }

}
