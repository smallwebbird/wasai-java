package com.example.wasai.config.redisConfig;

public interface RedisService {
    void set(String key, String value);

    void set(String key, String value, long expire);

    String get(String key);

    boolean expire(String key, long expire);

    void remove(String key);

    Long increment(String key, int delta);

    boolean hset(String key, String item, Object value, long expire);

    Object hget(String key, String field);

    void setTokenRefresh(String name, String token, Long expireTime, Long validTime, String jwtId);

    void delete(String key);

    Object hget(String key);

    Boolean hasKey(String key,String field);

    Boolean hasKey(String key);
}
