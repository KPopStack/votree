package com.toast.votree.controller.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.toast.votree.manager.DbShardingManager;
import com.toast.votree.service.ResponseCheckRedisService;
import com.toast.votree.sharding.ResponseCheckMapper;
import com.toast.votree.util.DbgUtil;
import com.toast.votree.vo.ResponseCheck;

@Controller
public class ResponseCheckController {

  @Autowired
  ResponseCheckRedisService responseCheckRedisService;
  @Autowired
  DbShardingManager dbShardingManager;

  private static final int URI_COUNT = 11;
  private static final String[] uri = {"/", "/detail", "/voting", "/result"
      , "/search", "/profiles", "/files", "/facebook", "/twitter", "/payco", "/logout"};
  
  @RequestMapping(value = "/check", method = RequestMethod.GET)
  public String requestCheck(Model model){
    Map<String, Object> resultMap = updateDataBase();
    model.addAttribute("currentResult", resultMap);
    return "response.check";
  }
  
  /*
   * 오늘 날짜를 제외한 REDIS 서버의 정보들이
   * 정리되어 디비에 업데이트 됩니다.
   * (이 함수가 실행되면 오늘 날짜의 정보들만 REDIS 서버에 남아있습니다..)
   */
  @SuppressWarnings("unchecked")
  private synchronized Map<String, Object> updateDataBase(){
    String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    long[] average = new long[URI_COUNT];
    long todayAverage;
    ResponseCheckMapper responseCheckMapper = dbShardingManager.getMapperInCommonDb(ResponseCheckMapper.class);
    Map<String, Object> result = new HashMap<>();
    Map<String, Object> currentResult = new HashMap<>();
    String tempDate, inputDate = null;
    long responseTime, sumOfResponseTime, todaySumOfResponseTime;
    int count;
    int todayCount;
    for(int j = 0 ; j < URI_COUNT ; j++ ){
      sumOfResponseTime = 0;
      todaySumOfResponseTime = 0;
      count = 0;
      todayCount = 0;
      long size = responseCheckRedisService.getDataSizeByKey(uri[j]);
      ArrayList<Long> array = new ArrayList<>();
      //long[] array = new long[size];
      DbgUtil.logger().debug("J LOOP : " + j);
      for(long i = 0 ; i < size ; i++) {
        result = (Map<String, Object>) responseCheckRedisService.findDataByKey(uri[j]); // RIGHT POP
        tempDate = (String) result.get("date");
        responseTime = (long) result.get("responseTime");
        /* 어제까지 */
        if( !currentDate.equals(tempDate) ){
          sumOfResponseTime += responseTime;
          count++;
          inputDate = tempDate;
          array.add(responseTime);
          //array[i] = responseTime;
        }
        /* 오늘 */
        else{
          responseCheckRedisService.createDataByKey(uri[j], result); //Left PUSH
          //DbgUtil.logger().debug("BREAK POINT");
          currentResult.put("result"+j, responseTime);
          todaySumOfResponseTime += responseTime;
          todayCount++;
          //break;
        }
      }
      
      if(sumOfResponseTime != 0 && count != 0){
        average[j] = sumOfResponseTime / count;
        long standardDeviation = standardDeviation(array, 1, count, average[j]);
        ResponseCheck responseCheck = new ResponseCheck()
              .setAverage(average[j])
              .setDate(inputDate)
              .setUri(uri[j])
              .setStandardDeviation(standardDeviation);
        responseCheckMapper.insertResponseTime(responseCheck);
        DbgUtil.logger().debug("DATA BASE ADDED : " + inputDate + " : " + uri[j] + " : " + average[j]);
      }
      if(todayCount != 0) {
        todayAverage = todaySumOfResponseTime / todayCount;
        currentResult.put("todayResult"+j, todayAverage);
        currentResult.put("todayCount"+j, todayCount);
      }
    }
    return currentResult;
  }
  
  public static long standardDeviation(ArrayList<Long> array, int option, int count, long average) {
    if (array.size() < 2) return 0;
    long sum = 0;
    long standardDeviation = 0;
    long diff;

    for (int i = 0; i < count; i++) {
      diff = array.get(i) - average;
      sum += diff * diff;
    }
    standardDeviation = (long) Math.sqrt(sum / (array.size() - option));

    return standardDeviation;
    }
}
