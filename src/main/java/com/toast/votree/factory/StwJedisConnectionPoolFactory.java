package com.toast.votree.factory;

import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import redis.clients.jedis.Jedis;

public class StwJedisConnectionPoolFactory extends JedisConnectionFactory {
  
  private static final int DEFAULT_REDIS_DATABASE_INDEX = 0;

  private int databaseIndex = 0;

  @Override
  protected Jedis fetchJedisConnector() {
      Jedis jedis = super.fetchJedisConnector();

      if (this.databaseIndex != DEFAULT_REDIS_DATABASE_INDEX) {
          jedis.select(this.databaseIndex);
      }

      return jedis;
  }

  public void setDatabaseIndex(int databaseIndex) {
      this.databaseIndex = databaseIndex;
  }
}