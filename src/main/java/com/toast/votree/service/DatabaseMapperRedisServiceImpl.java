package com.toast.votree.service;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.toast.votree.factory.StwJedisConnectionPoolFactory;

@Service("databaseMapperRedisService")
public class DatabaseMapperRedisServiceImpl implements DatabaseMapperRedisService {

  @Autowired
  RedisTemplate<String, Object> redisTemplate;
  @Resource(name="jedisConnectionFactory")
  StwJedisConnectionPoolFactory factory;
  
  public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public void setFactory(StwJedisConnectionPoolFactory factory) {
    this.factory = factory;
  }

  @Override
  public Object findDataByKeyInHashKey(String key, String hashKey) {
    moveToDBMapperIndex();
    Object dbObject = redisTemplate.opsForHash().get(key, hashKey);
    int dbIndex = (dbObject == null ? -1 : (int) dbObject);
    return dbIndex;
  }
  
  @Override
  public void createDataByKeyInHashKey(String key, String hashKey, Object value) {
    moveToDBMapperIndex();
    redisTemplate.persist(key);
    redisTemplate.opsForHash().put(key, hashKey, value);
  }
  
  @Override
  public HashOperations<String, Object, Object> opsForHash() {
    return redisTemplate.opsForHash();
  }
  
  @Override
  public boolean hasKey(String key) {
    moveToDBMapperIndex();
    return redisTemplate.hasKey(key);
  }

  private synchronized void moveToDBMapperIndex() {
    factory.setDatabaseIndex(1);
  }

  @Override
  public boolean expire(String key, long timeout, TimeUnit unit) {
    return false;
  }

  @Override
  public void delete(String key) {
    
  }

}
