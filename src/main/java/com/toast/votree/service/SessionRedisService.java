package com.toast.votree.service;


import org.springframework.stereotype.Service;

@Service
public interface SessionRedisService extends RedisService{
  
  public void opsHashForPutLoginUrl(String key, String hashKey, String value);
  
  public void opsHashForPutUser(String key, int userId, String userName);
  
}
