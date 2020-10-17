package com.glsx.plat.redis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * @author payu
 */
@Slf4j
@Service
public class GainIdService {

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 前缀 不同系统的前缀可以不相同
     *
     * @param prefix
     * @return
     */
    public String gainId(String prefix) {
        Long seq = redisTemplate.opsForValue().increment("system:idseq:" + prefix);
        String pattern = "000000";
        DecimalFormat df = new DecimalFormat(pattern);
        String str = df.format(seq);
        //substring(int beginIndex)，[beginIndex,endIndex]的子串，即获取后四位编号
        str = str.substring(str.length() - pattern.length());
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timeStr = sf.format(new Date());
        return prefix + timeStr + str;
    }

    /**
     * 生成编号,格式: prefix+yyyyMMdd+自增id
     * 例如:传入 B 则返回 B20190910000007
     *
     * @param prefix
     * @return
     */
    @SuppressWarnings("unchecked")
    public String globalUniqueId(String prefix) {
        String key = prefix + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count;
        if (redisTemplate.hasKey(key)) {
            count = generateId(key);
        } else { //第一次会设置过期时间为当天24:00:00
            Calendar todayEnd = Calendar.getInstance();
            todayEnd.set(Calendar.HOUR_OF_DAY, 23);
            todayEnd.set(Calendar.MINUTE, 59);
            todayEnd.set(Calendar.SECOND, 59);
            todayEnd.set(Calendar.MILLISECOND, 999);
            count = generateId(key, todayEnd.getTime());
        }
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
        return key + time + String.format("%06d", count);
    }

    private long generateId(String key) {
        RedisAtomicLong counter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
        return counter.incrementAndGet();
    }

    private long generateId(String key, Date expire) {
        RedisAtomicLong counter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
        counter.expireAt(expire);
        return counter.incrementAndGet();
    }

}