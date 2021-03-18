package cn.com.glsx.smartdevice.job;

import cn.com.glsx.smartdevice.service.CheckDayService;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.elasticjob.lite.annotation.ElasticSimpleJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author fengzhi
 * @date 2021/3/12 13:57
 * @description 定时将全年12个月的节假日，工作日入库
 */
@Slf4j
@Component
@ElasticSimpleJob(jobName = "WorkOrHoliDayJob",cron="0 0 0 1 * ?")
//定时每月1号0点
public class WorkOrHoliDayJob implements SimpleJob {
    @Autowired
    private CheckDayService checkDayComponent;

    @Override
    public void execute(ShardingContext shardingContext) {
        log.info("Jobname==="+shardingContext.getJobName());
        checkDayComponent.searchAndInsertMonth2();
    }
}
