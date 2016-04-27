package com.toast.votree.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.toast.votree.factory.StwJedisConnectionPoolFactory;
import com.toast.votree.manager.DbShardingManager;
import com.toast.votree.sharding.VotreeMapper;
import com.toast.votree.util.DbgUtil;
import com.toast.votree.vo.Votree;

@Service("hitCountWriteBackService")
public class HitCountWriteBackServiceImpl implements HitCountWriteBackService {
  @Autowired
  RedisTemplate<String, Object> redisTemplate;
  @Resource(name="jedisConnectionFactory")
  StwJedisConnectionPoolFactory factory;
  @Autowired
  DbShardingManager dbShardingManager;
  
  final String UPDATE_TIME = "updateTime";
  final String HIT_VALUE = "hitValue";
  
  final private int LOCATION_IN_REDIS_INDEX = 3;
  public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public void setFactory(StwJedisConnectionPoolFactory factory) {
    this.factory = factory;
  }
  
  public HitCountWriteBackServiceImpl() {
  }

  @Override
  public void hitCountWriteBack(String votreeId) {
    VotreeMapper votreeMapper = dbShardingManager.getMapper(votreeId, VotreeMapper.class);
    moveToDBMapperIndex();
    
    DbgUtil.logger().debug("votreeid:" + votreeId);
    Timestamp temp = (Timestamp) redisTemplate.opsForHash().get(votreeId, UPDATE_TIME);
    if(temp == null) {
      redisTemplate.opsForHash().put(votreeId, UPDATE_TIME, getCurrentTimeStamp());
      redisTemplate.opsForHash().put(votreeId, HIT_VALUE, 1);
      DbgUtil.logger().debug("temp = votreeid:" + votreeId);
    } else {
      Timestamp updateTimeStamp = getUpdatePlusFiveMinuteTimeStamp(temp.toInstant());
      int currentHitCount = (int) redisTemplate.opsForHash().get(votreeId, HIT_VALUE);
      DbgUtil.logger().debug("currentHitCount:" + currentHitCount);
      currentHitCount += 1;
      
      DbgUtil.logger().debug("temp != null :" + updateTimeStamp);
      if( getCurrentTimeStamp().getTime() > updateTimeStamp.getTime() ) {
        DbgUtil.logger().debug("getTime() > updateTimeStamp:" + updateTimeStamp);
        Votree updateVotree = new Votree().setId(votreeId).setHit(currentHitCount);
        votreeMapper.updateVotreeHit(updateVotree);
        redisTemplate.opsForHash().put(votreeId, UPDATE_TIME, getCurrentTimeStamp());
      }
      redisTemplate.opsForHash().put(votreeId, HIT_VALUE, currentHitCount);
    }
  }
  private synchronized void moveToDBMapperIndex() {
    factory.setDatabaseIndex(LOCATION_IN_REDIS_INDEX);
  }
  
  private Timestamp getUpdatePlusFiveMinuteTimeStamp(Instant updateInstant)
  {
    ZonedDateTime updateZoneDateTime = ZonedDateTime.ofInstant(updateInstant, ZoneId.of("Z"));
    Timestamp updateTimeStamp = Timestamp.from(updateZoneDateTime.plusMinutes(1).toInstant());
    return updateTimeStamp;
  }
  
  private Timestamp getCurrentTimeStamp()
  {
    Instant nowTimeInstant = Instant.now();
    ZonedDateTime nowZoneDateTime = ZonedDateTime.ofInstant(nowTimeInstant, ZoneId.of("Z"));
    Timestamp currentTimeStamp = Timestamp.from(nowZoneDateTime.toInstant());
    return currentTimeStamp;
  }
}
