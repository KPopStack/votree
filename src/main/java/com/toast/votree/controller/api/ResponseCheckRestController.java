package com.toast.votree.controller.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.toast.votree.manager.DbShardingManager;
import com.toast.votree.sharding.ResponseCheckMapper;
import com.toast.votree.util.DbgUtil;

@RestController
public class ResponseCheckRestController {
  
  @Autowired
  DbShardingManager dbShardingManager;
  
  private static final String[] uri = {"/", "/detail", "/voting", "/result"
      , "/search", "/profiles"};
  
  @RequestMapping(value="/api/v0.1/check/{type}",  method = RequestMethod.GET)
  public Map<String,Object> getResponseCheckDataForChart(
      @PathVariable(value="type") String type
      , @RequestParam(value="year") String year
      , @RequestParam(value="month") String month) {
    
    String scope = year + "-" + month;
    ResponseCheckMapper responseCheckMapper = dbShardingManager.getMapperInCommonDb(ResponseCheckMapper.class);
    ArrayList<Object> list = new ArrayList<>();
    Map<String, Object> map = new HashMap<>();
    Map<String, String> paramUriAndScope = new HashMap<>();
    for(String paramUri : uri){
      paramUriAndScope.put("uri", paramUri);
      paramUriAndScope.put("scope", scope);
      if(type.equals("average")){
        map = responseCheckMapper.selectAverageDatumForChart(paramUriAndScope);
      }
      else if(type.equals("sd")){
        map = responseCheckMapper.selectSdDatumForChart(paramUriAndScope);
      }
      if(map != null) {
        map.put("name", paramUri);
        list.add(map);
      }
    }
    DbgUtil.logger().debug("LIST FOR LINE CHART : " + list);
    Map<String, String> paramScope = new HashMap<>();
    paramScope.put("scope", scope);
    ArrayList<String> categories = responseCheckMapper.selectCategoriesForChart(paramScope);
    
    Map<String, Object> result = new HashMap<>();
    result.put("series", list);
    result.put("categories", categories);
    
    return result;
    
  }

}
