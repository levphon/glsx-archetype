package com.glsx.plat.loggin.strategy;

import com.glsx.plat.loggin.AbstractLogginStrategy;
import com.glsx.plat.loggin.entity.SysLogEntity;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component("mongodb")
public class LogginStrategyByMongoDB extends AbstractLogginStrategy {

    @Autowired(required = false)
    private MongoTemplate mongoTemplate;

    @Override
    public void saveLog(SysLogEntity entity) {
        mongoTemplate.insert(entity);
    }

    @Async
    @Override
    public void updateLogStatus(String logTraceId, String result) {
        Query query = new Query(Criteria.where("_id").is(logTraceId));
        Update update = new Update().set("result", result);
        UpdateResult ur = mongoTemplate.updateFirst(query, update, SysLogEntity.class);
//        long count = ur.getModifiedCount();
//        log.info("更新记录数：" + count);
    }

}
