package com.toast.votree.util;

import java.io.IOException;
import java.util.HashMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.toast.votree.exception.JsonIsNullException;

public class JsonUtil {

  static ObjectMapper mapper = new ObjectMapper();

  public static String toJsonString(Object obj) {
    try{
      return mapper.writeValueAsString(obj);
    } catch(IOException e){
      throw new RuntimeException(e);
    }
  }
  public static String toJsonPretty(Object obj) {
    try{
      return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static <T> T readJson(String jsonString, Class<T> valueType) {
    try{
      return mapper.readValue(jsonString, valueType);
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static HashMap<String, Object> jsonToMap(String jsonString) {
    try{
      TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};
      return mapper.readValue(jsonString, typeRef);
    } catch(IOException e) {
      throw new RuntimeException(e);
    } catch(NullPointerException e) {
      throw new JsonIsNullException(e);
    }
  }
}
