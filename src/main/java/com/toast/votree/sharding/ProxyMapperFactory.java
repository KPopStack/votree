package com.toast.votree.sharding;

import org.springframework.cglib.proxy.Proxy;

import com.toast.votree.sharding.handler.DbShardingMapperHandler;

public class ProxyMapperFactory {
  @SuppressWarnings("rawtypes")
  private static final Class[] targetClasses = new Class[] {
      VotreeMapper.class, VoteMapper.class, VoteItemMapper.class, VoteResultMapper.class, VotingMapper.class};
  
  public static Object newInstance(Object target) {
    return Proxy.newProxyInstance(target.getClass().getClassLoader(), targetClasses, new DbShardingMapperHandler(target));
  }
}
