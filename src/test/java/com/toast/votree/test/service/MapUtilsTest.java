package com.toast.votree.test.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.Test;

import com.toast.votree.util.MapUtils;

import junit.framework.Assert;

public class MapUtilsTest {

  @Test
  public void testSortByValue() {
      Random random = new Random(System.currentTimeMillis());
      Map<String, Integer> testMap = new HashMap<String, Integer>(1000);
      for(int i = 0 ; i < 1000 ; ++i) {
          testMap.put( "SomeString" + random.nextInt(), random.nextInt());
      }

      testMap = MapUtils.sortByValue( testMap );
      Assert.assertEquals( 1000, testMap.size() );

      Integer previous = null;
      for(Map.Entry<String, Integer> entry : testMap.entrySet()) {
          Assert.assertNotNull( entry.getValue() );
          if (previous != null) {
              Assert.assertTrue( entry.getValue() >= previous );
          }
          previous = entry.getValue();
      }
  }

}
