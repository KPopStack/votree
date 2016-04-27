package com.toast.votree.service;

import org.springframework.data.redis.core.ListOperations;

public interface ResponseCheckRedisService extends RedisService{

  public ListOperations<String, Object> opsForList();
  
  public void createDataByKey(String key, Object value);
  
  public Object findDataByKey(String key);

  String findStringDataByKey(String key);

  long getDataSizeByKey(String key);
}
