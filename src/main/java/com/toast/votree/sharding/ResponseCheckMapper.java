package com.toast.votree.sharding;

import java.util.ArrayList;
import java.util.Map;

import com.toast.votree.vo.ResponseCheck;

public interface ResponseCheckMapper {
  public int insertResponseTime(ResponseCheck responseCheck);
  public Map<String, Object> selectAverageDatumForChart(Map<String, String> map);
  public Map<String, Object> selectSdDatumForChart(Map<String, String> map);
  public ArrayList<String> selectCategoriesForChart(Map<String, String> map);
}
