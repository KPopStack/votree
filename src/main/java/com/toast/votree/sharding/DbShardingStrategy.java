package com.toast.votree.sharding;

public interface DbShardingStrategy {
  int findDbIndexByShardingKey(String shardingKey);
  int mappingDbShardingIndex(String shardingKey, DbShardingMapperAccessor accessor); 
  String generateShardingKey();
}
