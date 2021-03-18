package cn.com.glsx.smartdevice.job;


import cn.com.glsx.smartdevice.factory.Condition;
import cn.com.glsx.smartdevice.service.SceneService;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.elasticjob.lite.annotation.ElasticSimpleJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author fengzhi
 * @date 2021/3/12 13:57
 */
@Slf4j
@Component
@ElasticSimpleJob(jobName = "TimerTriggerAllJob",cron="0/10 * * * * ?")
public class TimerTriggerAllJob implements SimpleJob {
    @Autowired
    private SceneService sceneComponent;


    @Override
    public void execute(ShardingContext shardingContext) {
        log.info("Jobname==="+shardingContext.getJobName());
        //定时每10秒执行
        sceneComponent.execute(Condition.ALL_MATCH.getCode());
    }
}
