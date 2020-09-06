package com.example.wasai.config.redisConfig;

import com.example.wasai.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

@Component
public class RedisServiceImpl implements RedisService {
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Override
    public void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void set(String key, String value, long expire) {
        stringRedisTemplate.opsForValue().set(key, value, expire, TimeUnit.SECONDS);
    }


    @Override
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    @Override
    public boolean expire(String key, long expire) {
        return stringRedisTemplate.expire(key, expire, TimeUnit.SECONDS);
    }

    @Override
    public void remove(String key) {
        stringRedisTemplate.delete(key);
    }

    @Override
    public Long increment(String key, int delta) {
        return stringRedisTemplate.opsForValue().increment(key,delta);
    }

    @Override
    public boolean hset(String key, String item, Object value, long expire) {
        try {
            stringRedisTemplate.opsForHash().put(key, item, value);
            expire(key, expire);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 查询key和field所确定的值
     *
     * @param key 查询的key
     * @param field 查询的field
     * @return HV
     */
    public Object hget(String key,String field) {
        return stringRedisTemplate.opsForHash().get(key, field);
    }

    /**
     * 删出key
     * 这里跟下边deleteKey（）最底层实现都是一样的，应该可以通用
     * @param key
     * */
    public void delete(String key){
        stringRedisTemplate.opsForValue().getOperations().delete(key);
    }

    /**
     * 查询该key下所有值
     *
     * @param key 查询的key
     * @return Map<HK, HV>
     */
    public Object hget(String key) {
        return stringRedisTemplate.opsForHash().entries(key);
    }

    /**
     * 判断key和field下是否有值
     *
     * @param key 判断的key
     * @param field 判断的field
     */
    public Boolean hasKey(String key,String field) {
        return stringRedisTemplate.opsForHash().hasKey(key,field);
    }


    /**
     * 判断key下是否有值
     *
     * @param key 判断的key
     */
    public Boolean hasKey(String key) {
        return stringRedisTemplate.opsForHash().getOperations().hasKey(key);
    }


    @Override
    public void setTokenRefresh(String userName, String token, Long expireTime, Long validTime, String jwtId) {
        hset(userName, "jwtValidTime", DateUtil.getJWTValidTime(validTime), expireTime);
        hset(userName, "jwtExpirationTime", DateUtil.getJWTExpiration(expireTime), expireTime);
        hset(userName, "jwtToken", token, expireTime);
        hset(userName, "jwtId", jwtId, expireTime);
    }
}
