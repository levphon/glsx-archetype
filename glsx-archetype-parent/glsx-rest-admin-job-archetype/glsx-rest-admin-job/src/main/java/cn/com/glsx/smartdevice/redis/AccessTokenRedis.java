package cn.com.glsx.smartdevice.redis;

import cn.com.glsx.modules.model.param.AccessToken;
import org.springframework.stereotype.Component;

@Component
public class AccessTokenRedis extends RedisService {

    public AccessToken readCache(String key){
        Object alarmObject = redisTemplate.opsForValue().get(key);
        return (AccessToken) alarmObject;
    }

    public void add(String key,AccessToken value){
        redisTemplate.opsForValue().set(key,value);
    }
}
