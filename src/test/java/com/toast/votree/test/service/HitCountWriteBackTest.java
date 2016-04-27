package com.toast.votree.test.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.toast.votree.service.HitCountWriteBackService;
import com.toast.votree.service.VotreeService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
    "classpath:spring/applicationContext-test.xml",
    "classpath:spring/servlet-context.xml"
})
public class HitCountWriteBackTest {

  @Autowired
  HitCountWriteBackService hitCountWriteBackService;
  
  @Mock
  VotreeService votreeService;
  Map<String, Object> value = new HashMap<>();
  @Before
  public void setup() {
    LocalDateTime superDay = LocalDateTime.of(2016, 2, 5, 15, 0, 0);
    ZonedDateTime zoneDay = superDay.atZone(ZoneId.systemDefault());
    
    value.put("updateTime", zoneDay);
    value.put("hitValue", 2);
  }
  
  @Test
  public void test() {
    hitCountWriteBackService.hitCountWriteBack("1457671270568_2762fed970ac47368e16d15f507745d9");
  }
}