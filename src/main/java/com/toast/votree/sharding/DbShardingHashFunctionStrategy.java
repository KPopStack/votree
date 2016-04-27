package com.toast.votree.sharding;

import java.util.UUID;

public class DbShardingHashFunctionStrategy implements DbShardingStrategy {
  
  final private int SHARD_DB_COUNT = 2;
  final private int SHARD_DB_NOT_AVAILABLE = -1;

  public DbShardingHashFunctionStrategy() {
    // TODO Auto-generated constructor stub
  }

  @Override
  public int findDbIndexByShardingKey(String shardingKey) {
    return calcSumOfShardingKey(shardingKey) % SHARD_DB_COUNT;
  }

  @Override
  public int mappingDbShardingIndex(String shardingKey, DbShardingMapperAccessor accessor) {
    for (int i = 0; i < SHARD_DB_COUNT; i++) {
      int dbIndex = (calcSumOfShardingKey(shardingKey)+i) % SHARD_DB_COUNT;
      if (accessor.isAvailableDbLocation(dbIndex)) {
        return dbIndex;
      }
    }
    
    return SHARD_DB_NOT_AVAILABLE;
  }
  
  @Override
  public String generateShardingKey() {
    return System.currentTimeMillis()+"_"+UUID.randomUUID().toString().replace("-", "");
  }

  private int calcSumOfShardingKey(String shardingKey) {
    int sum = 0;
    for (char ch : shardingKey.toCharArray()) {
      sum =+ ch;
    }
    return sum;
  }
}
