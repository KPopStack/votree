package com.toast.votree.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.stereotype.Service;

@Service
public interface RedisService {
  
  public Object findDataByKeyInHashKey(String key, String hashKey);
  
  public void createDataByKeyInHashKey(String key, String hashKey, Object value);
  
  public HashOperations<String, Object, Object> opsForHash();
  
  public boolean hasKey(String key);
  
  public boolean expire(String key, long timeout, TimeUnit unit);
  
  public void delete(String key);
  
}
