package com.glsx.plat.redis.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * redisTemplate封装
 */
@Slf4j
@Component
public class RedisUtils {

    @Autowired
    @Qualifier("restRedisTemplate")
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Boolean exists(byte[] key) {
        Object execute = redisTemplate.execute((RedisCallback<Boolean>) connection -> connection.exists(key));
        return (Boolean) execute;
    }

    /**
     * 指定缓存失效时间
     *
     * @param key     键
     * @param seconds 时间(秒)
     * @return
     */
    public boolean expire(String key, long seconds) {
        try {
            if (seconds > 0) {
                redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }


    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    @SuppressWarnings("unchecked")
    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }

    public void del(byte[] key) {
        redisTemplate.execute((RedisCallback) connection -> {
            connection.del(key);
            return null;
        });
    }

    //============================String=============================

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    public byte[] get(byte[] key) {
        Object execute = redisTemplate.execute((RedisCallback<byte[]>) connection -> connection.get(key));
        return (byte[]) execute;
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key     键
     * @param value   值
     * @param seconds 时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean setex(String key, Object value, long seconds) {
        try {
            if (seconds > 0) {
                redisTemplate.opsForValue().set(key, value, seconds, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setex(byte[] key, byte[] value, int seconds) {
        redisTemplate.execute((RedisCallback) connection -> {
            connection.setEx(key, seconds, value);
            return null;
        });
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return
     */
    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     * @return
     */
    public long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    //================================Map=================================

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public Object hget(String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public <T> Map<Object, Object> hmgetT(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public boolean hmset(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public <T> boolean hmsetT(String key, Map<String, T> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     *
     * @param key     键
     * @param map     对应多个键值
     * @param seconds 时间(秒)
     * @return true成功 false失败
     */
    public boolean hmset(String key, Map<String, Object> map, long seconds) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (seconds > 0) {
                expire(key, seconds);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key     键
     * @param item    项
     * @param value   值
     * @param seconds 时间(秒)  注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value, long seconds) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (seconds > 0) {
                expire(key, seconds);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(String key, Object... item) {
        redisTemplate.opsForHash().delete(key, item);
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     * @return
     */
    public double hincr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     * @return
     */
    public double hdecr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }

    //============================set=============================

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return
     */
    public Set<Object> sGet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean sHasKey(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSet(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 将set数据放入缓存
     *
     * @param key     键
     * @param seconds 时间(秒)
     * @param values  值 可以是多个
     * @return 成功个数
     */
    public long sSetAndTime(String key, long seconds, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (seconds > 0) {
                expire(key, seconds);
            }
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return
     */
    public long sGetSetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public long setRemove(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    //===============================list=================================

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束  0 到 -1代表所有值
     * @return
     */
    public List<Object> lGet(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取list缓存的内容
     *
     * @param key 键
     * @return
     */
    public <T> List<T> lGet(String key) {
        try {
            return (List<T>) redisTemplate.opsForList().range(key, 0, -1);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return
     */
    public long lGetListSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引  index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public Object lGetIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(String key, Object value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, List<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(String key, List<Object> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return
     */
    public boolean lUpdateIndex(String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public long lRemove(String key, long count, Object value) {
        try {
            Long remove = redisTemplate.opsForList().remove(key, count, value);
            return remove;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获得缓存的基本对象列表
     *
     * @param pattern 字符串前缀
     * @return 对象列表
     */
    public Collection<String> keys(final String pattern) {
        return redisTemplate.keys(pattern);
    }

    /**
     * 删除集合对象
     *
     * @param collection 多个对象
     * @return
     */
    public long del(final Collection collection) {
        return redisTemplate.delete(collection);
    }

    /**
     * 分布式加锁
     *
     * @param key     key
     * @param value   value
     * @param seconds 过期时间（秒）
     * @return
     */
    public boolean setnx(String key, Object value, long seconds) {
        //SET key value [EX seconds] [PX milliseconds] [NX|XX]
        return redisTemplate.opsForValue().setIfAbsent(key, value, seconds, TimeUnit.SECONDS);
    }

    /**
     * 分布式解锁
     * 释放锁的时候，有可能因为持锁之后方法执行时间大于锁的有效期，此时有可能已经被另外一个线程持有锁，所以不能直接删除
     *
     * @param key
     * @param value
     */
    public boolean unlock(String key, Object value) {
        Object currentValue = this.get(key);
        try {
            if (!StringUtils.isEmpty(currentValue) && currentValue.equals(value)) {
                this.del(key);
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 添加指令：geoadd 指令携带集合名称以及多个经纬度名称三元组，注意这里可以加入多个三元组
     *
     * @param key
     * @param longitude
     * @param latitude
     * @param member
     * @return
     */
    public Long geoadd(String key, double longitude, double latitude, String member) {
        Long addedCnt = redisTemplate.opsForGeo().add(key, new Point(longitude, latitude), member);
        return addedCnt;
    }

    /**
     * 获取元素位置：geopos 指令可以获取集合中任意元素的经纬度坐标，可以一次获取多个
     *
     * @param key
     * @param members
     * @return
     */
    public List<Point> geopos(String key, String... members) {
        List<Point> points = redisTemplate.opsForGeo().position(key, members);
        return points;
    }

    /**
     * 计算距离：geodist 指令可以用来计算两个元素之间的距离，携带集合名称、2 个名称和距离单位
     *
     * @param key
     * @param member1
     * @param member2
     * @return
     */
    public Distance geodist(String key, String member1, String member2) {
        Distance distance = redisTemplate.opsForGeo().distance(key, member1, member2, RedisGeoCommands.DistanceUnit.KILOMETERS);
        return distance;
    }

    /**
     * 获取元素的 hash 值：geohash 可以获取元素的经纬度编码字符串，上面已经提到，它是 base32 编码。 你可以使用这个编码值去 geohash.org/${hash}中进行直… geohash 的标准编码值
     *
     * @param key
     * @param places
     * @return
     */
    public List<String> geohash(String key, String... places) {
        List<String> results = redisTemplate.opsForGeo().hash(key, places);
        return results;
    }

    /**
     * 查询附近的公司：georadiusbymember 指令是最为关键的指令，它可以用来查询指定元素附近的其它元素
     *
     * @param key
     * @param distanceVal
     * @param member
     * @param limit
     * @return
     */
    public GeoResults<RedisGeoCommands.GeoLocation<Object>> georadius(String key, double distanceVal, String member, long limit) {
        Distance distance = new Distance(distanceVal, Metrics.KILOMETERS);
        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeDistance().includeCoordinates().sortAscending().limit(limit);
        GeoResults<RedisGeoCommands.GeoLocation<Object>> results = redisTemplate.opsForGeo().radius(key, member, distance, args);
        return results;
    }

    /**
     * 根据坐标查询附近的元素：它可以根据用户的定位来计算「附近的车」，「附近的餐馆」等。它的参数和 georadiusbymember 基本一致，除了将目标元素改成经纬度坐标值
     *
     * @param key
     * @param longitude
     * @param latitude
     * @param limit
     * @return
     */
    public GeoResults<RedisGeoCommands.GeoLocation<Object>> georadius(String key, double longitude, double latitude, long limit) {
        Circle circle = new Circle(longitude, latitude, Metrics.KILOMETERS.getMultiplier());
        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeDistance().includeCoordinates().sortAscending().limit(limit);
        GeoResults<RedisGeoCommands.GeoLocation<Object>> results = redisTemplate.opsForGeo().radius(key, circle, args);
        return results;
    }

}