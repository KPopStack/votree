package com.toast.votree.test.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.junit.Before;
import org.junit.Test;

import com.toast.votree.util.DbgUtil;

public class ZoneDateTimeTest {

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void test() {
    LocalDateTime time = LocalDateTime.now();
    Timestamp nowTime = Timestamp.valueOf(time);
    
    String expireTime = "2016-02-17T06:13:30" + "+09:00[Asia/Seoul]";
    
    String expireTime1 = "2016-02-17T06:13:30Z";
    
    String expireTime2 = "2016-02-17T06:13:30";
    
    ZonedDateTime zoneDateTime = ZonedDateTime.parse(expireTime1);
//    LocalDateTime localDatetime = zoneDateTime.toLocalDateTime();

    ZonedDateTime baseDateTime =  ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("Z"));
//    DbgUtil.logger().debug("ZoneOffset.systemDefault():" + ZoneOffset.systemDefault());

    
    ZonedDateTime nowDateTime = ZonedDateTime.now();
    LocalDateTime localDatetime = nowDateTime.toLocalDateTime();
    ZonedDateTime result = ZonedDateTime.ofLocal(localDatetime.minusSeconds(nowDateTime.getOffset().getTotalSeconds()), ZoneId.of("Z"), ZoneOffset.UTC);
//    DbgUtil.logger().debug(result.toString());
    
    Instant instant = result.toInstant();
    Timestamp timestampFromInstant = Timestamp.from(instant);
    Instant intantFromTimestamp = timestampFromInstant.toInstant();
    
    DbgUtil.logger().debug(timestampFromInstant.getTime()+"!!!");
    
    Instant instant1 = Instant.now();
    Timestamp timestampFromInstant1 = Timestamp.from(instant1);
    ZonedDateTime test = ZonedDateTime.ofInstant(instant1, ZoneId.of("Z"));
    ZonedDateTime test2 = ZonedDateTime.ofInstant(instant1, ZoneId.systemDefault());
    
    DbgUtil.logger().debug(test.toString());
    DbgUtil.logger().debug(test2.toString());
    
    DbgUtil.logger().debug(Timestamp.from(test.plusMinutes(30).toInstant())+"");
    
    
    
    
//    Timestamp testtime = Timestamp.valueOf(expireTime);
    
  }

}
