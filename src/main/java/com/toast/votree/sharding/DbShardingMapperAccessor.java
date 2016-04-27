package com.toast.votree.sharding;

import java.util.List;

public interface DbShardingMapperAccessor {
  public <T> T getMapper(String shardingKey, Class<T> mapperType);
  public <T> List<T> getMappers(Class<T> mapperType);
  
  public <T> T getMapperInCommonDb(Class<T> mapperType);
  public boolean isAvailableDbLocation(int dbIndex); 
}
