package com.glsx.plat.redis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Slf4j
@Service
public class GainIdService {

    @Resource
    private RedisTemplate redisTemplate;

    //前缀 不同系统的前缀可以不相同
    public String gainId(String prefix) {
        Long seq = redisTemplate.opsForValue().increment("system:idseq:" + prefix);
        DecimalFormat df = new DecimalFormat("0000");
        String str = df.format(seq);
        //substring(int beginIndex)，[beginIndex,endIndex]的子串，即获取后四位编号
        str = str.substring(str.length() - 4);
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timeStr = sf.format(new Date());
        return prefix + timeStr + str;
    }

    public boolean addRedisLock(String redisLockMaxOrderNumber, int i, int i1) {
        return false;
    }

    public void delRedisLock(String redisLockMaxOrderNumber) {

    }

    /**
     * Redis生成数据库全局唯一性id
     *
     * @return
     */
    public Long getGlobalUniqueId() {
        String orderIdPrefix = this.getOrderIdPrefix(new Date());
        Long id = this.orderId(orderIdPrefix);
        return id;
    }

    /**
     * 获取年的后两位加上一年多少天+当前小时数作为前缀
     *
     * @param date
     * @return
     */
    public String getOrderIdPrefix(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        //补两位,因为一年最多三位数
        String monthFormat = String.format("%1$02d", month + 1);
        //补两位，因为日最多两位数
        String dayFormat = String.format("%1$02d", day);
        //补两位，因为小时最多两位数
        String hourFormat = String.format("%1$02d", hour);
        return year + monthFormat + dayFormat + hourFormat;
    }

    /**
     * 生成订单
     *
     * @param prefix
     * @return
     */
    public Long orderId(String prefix) {
        String key = "B" + prefix;
        String orderId = null;
        try {
            Long increment = redisTemplate.opsForValue().increment(key, 1);
            //往前补6位
            orderId = prefix + String.format("%1$06d", increment);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Long.valueOf(orderId);
    }
}