package com.toast.votree.service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.toast.votree.factory.StwJedisConnectionPoolFactory;
import com.toast.votree.util.DbgUtil;

@Service("responseCheckRedisService")
public class ResponseCheckRedisServiceImpl implements ResponseCheckRedisService{

  @Autowired
  RedisTemplate<String, Object> redisTemplate;
  @Resource(name="jedisConnectionFactory")
  StwJedisConnectionPoolFactory factory;
  
  public RedisTemplate<String, Object> getRedisTemplate() {
    return redisTemplate;
  }

  public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public StwJedisConnectionPoolFactory getFactory() {
    return factory;
  }

  public void setFactory(StwJedisConnectionPoolFactory factory) {
    this.factory = factory;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Object findDataByKey(String key) {
    moveToDBMapperIndex();
    Object responseObject = redisTemplate.opsForList().rightPop(key);
    Map<String, Object> map = (Map<String, Object>) responseObject;
    return map;
  }
  
  @Override
  public long getDataSizeByKey(String key) {
      moveToDBMapperIndex();
      return redisTemplate.opsForList().size(key);
  }
  
  @Override
  public String findStringDataByKey(String key) {
      moveToDBMapperIndex();
      long pathListSize = redisTemplate.opsForList().size(key);
      for(long i = 0 ; i < pathListSize ; i++) {
          DbgUtil.logger().debug( i + " : " + redisTemplate.opsForList().index(key, i));
      }
      //String response = (String) redisTemplate.opsForList().rightPop(key);
      return "";
  }

  @Override
  public void createDataByKey(String key, Object value) {
    moveToDBMapperIndex();
    redisTemplate.opsForList().leftPush(key, value);
  }

  @Override
  public ListOperations<String, Object> opsForList() {
    return redisTemplate.opsForList();
  }

  @Override
  public boolean hasKey(String key) {
    moveToDBMapperIndex();
    return redisTemplate.hasKey(key);
  }
  private synchronized void moveToDBMapperIndex() {
    factory.setDatabaseIndex(2);
  }
  
  
  @Override
  public boolean expire(String key, long timeout, TimeUnit unit) {
    return false;
  }

  @Override
  public void delete(String key) {
  }

  @Override
  public HashOperations<String, Object, Object> opsForHash() {
    return null;
  }

  @Override
  public Object findDataByKeyInHashKey(String key, String hashKey) {
    return null;
  }

  @Override
  public void createDataByKeyInHashKey(String key, String hashKey, Object value) {
  }

}
