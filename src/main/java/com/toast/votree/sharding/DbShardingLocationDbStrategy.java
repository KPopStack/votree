package com.toast.votree.sharding;

import java.util.UUID;

import com.toast.votree.service.DatabaseMapperRedisService;
import com.toast.votree.util.DbgUtil;

public class DbShardingLocationDbStrategy implements DbShardingStrategy {

//  @Resource(name="databaseMapperRedisService")
  DatabaseMapperRedisService databaseMapperRedisService;

  public void setDatabaseMapperRedisService(DatabaseMapperRedisService databaseMapperRedisService) {
    this.databaseMapperRedisService = databaseMapperRedisService;
  }
  final private int SHARD_DB_COUNT = 2;
  final private int SHARD_DB_NOT_AVAILABLE = -1;
  final private String REDIS_SHARDING_HASHKEY = "dbIndex";

  private static int shardIndex = 0;

  public DbShardingLocationDbStrategy() {
    // TODO Auto-generated constructor stub
  }

  @Override
  public int findDbIndexByShardingKey(String shardingKey) {
    Object dbObject = databaseMapperRedisService.findDataByKeyInHashKey(shardingKey, REDIS_SHARDING_HASHKEY);
    int dbIndex = (dbObject == null ? -1 : (int) dbObject);
    return dbIndex;
  }

  @Override
  public int mappingDbShardingIndex(String shardingKey, DbShardingMapperAccessor accessor) {
    for (int i = 0; i < SHARD_DB_COUNT; i++) {
      int dbIndex = getShardIndex();
      DbgUtil.logger().debug("db is avail check:" + dbIndex);
      if (accessor.isAvailableDbLocation(dbIndex)) {
        databaseMapperRedisService.createDataByKeyInHashKey(shardingKey, "dbIndex", dbIndex);
        return dbIndex;
      }
    }
    
    return SHARD_DB_NOT_AVAILABLE;
  }

  @Override
  public String generateShardingKey() {
    return System.currentTimeMillis() + "_" + UUID.randomUUID().toString().replace("-", "");
  }

  private synchronized int getShardIndex() {
    // 추후 각 디비의 레코드 사용량을 체크하여 더 효율적으로 분배하는 방법으로 확장
    shardIndex++;
    if (shardIndex > SHARD_DB_COUNT) {
      shardIndex = 1;
    }
    return shardIndex % SHARD_DB_COUNT;
  }
}
