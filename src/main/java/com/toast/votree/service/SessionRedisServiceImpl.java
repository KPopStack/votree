package com.toast.votree.service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.toast.votree.factory.StwJedisConnectionPoolFactory;
import com.toast.votree.util.DbgUtil;

@Service("sessionRedisService")
public class SessionRedisServiceImpl implements SessionRedisService  {
  
  @Autowired
  RedisTemplate<String, Object> redisTemplate;
  @Autowired
  StwJedisConnectionPoolFactory  factory;
  @Override
  public Object findDataByKeyInHashKey(String key, String hashKey) {
    moveToSessionIndex();
    return redisTemplate.opsForHash().get(key, hashKey);
  }
  
  @Override
  public void createDataByKeyInHashKey(String key, String hashKey, Object value) {
    moveToSessionIndex();
    redisTemplate.opsForHash().put(key, hashKey, value);
  }
  
  @Override
  public HashOperations<String, Object, Object> opsForHash() {
    return redisTemplate.opsForHash();
  }
  
  @Override
  public void delete(String key) {
    moveToSessionIndex();
    redisTemplate.delete(key);
  }

  @Override
  public boolean hasKey(String key) {
    moveToSessionIndex();
    return redisTemplate.hasKey(key);
  }

  @Override
  public boolean expire(String key, long timeout, TimeUnit unit) {
    moveToSessionIndex();
    return redisTemplate.expire(key, timeout, TimeUnit.HOURS);
  }

  @Override
  public synchronized void opsHashForPutLoginUrl(String key, String hashKey, String value) {
    moveToSessionIndex();
    final int REDIS_EXPIRE_TIME = 5;
    redisTemplate.expire(key, REDIS_EXPIRE_TIME, TimeUnit.MINUTES);
    redisTemplate.opsForHash().put(key, hashKey, value);
  }

  @Override
  public void opsHashForPutUser(String key, int userId, String userName) {
    moveToSessionIndex();
    redisTemplate.opsForHash().put(key, "userId", userId);
    redisTemplate.opsForHash().put(key, "userName", userName);
  }

  private synchronized void moveToSessionIndex(){
    factory.setDatabaseIndex(0);
  }

}
