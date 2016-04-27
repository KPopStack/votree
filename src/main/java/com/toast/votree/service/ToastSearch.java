package com.toast.votree.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.toast.votree.util.DbgUtil;

import net.minidev.json.JSONObject;

public class ToastSearch {
  private String indexApiUrl;
  private String searchApiUrl;
  
  public String getIndexApiUrl() {
    return indexApiUrl;
  }

  public void setIndexApiUrl(String indexApiUrl) {
    this.indexApiUrl = indexApiUrl;
  }

  public String getSearchApiUrl() {
    return searchApiUrl;
  }

  public void setSearchApiUrl(String searchApiUrl) {
    this.searchApiUrl = searchApiUrl;
  }
  
  protected void sendRequestToToastSearch(File jsonFile) {
    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
    builder.addBinaryBody("file", jsonFile);
    HttpEntity multipart = builder.build();
    HttpPost httpPost = new HttpPost(getIndexApiUrl());
    httpPost.setEntity(multipart);
    HttpClient client = HttpClients.createDefault();
    try {
      HttpResponse response = client.execute(httpPost);
      DbgUtil.logger().debug("INDEXING STATUS : " + response.getEntity());
    } catch (ClientProtocolException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    jsonFile.delete();
  }
  
  public List<String> getIdsByInput(String input) {
    HttpUriRequest httpUriRequest = RequestBuilder.create("GET")
        .setUri(getSearchApiUrl())
        .setEntity(new StringEntity(getSearchDocument(input), Charset.forName("utf-8")))
        .build();
    HttpClient client = HttpClients.createDefault();
    HttpResponse response;
    try {
      response = client.execute(httpUriRequest);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return getIdsByResponse(response);
  }
  
  protected Map<String, Object> getMapFromHttpResponse(HttpResponse response) {
    HttpEntity data =  response.getEntity();
    ObjectMapper mapper = new ObjectMapper();
    Map<String, Object> map;
    try {
      map = mapper.readValue(EntityUtils.toString(data), new TypeReference<Map<String, Object>>() {});
    } catch (ParseException | IOException e) {
      throw new RuntimeException(e);
    }
    return map;
  }
  
  @SuppressWarnings("unchecked")
  protected List<String> getIdsByResponse(HttpResponse response) {
    List<String> ids = new ArrayList<>();
    Map<String, Object> map = getMapFromHttpResponse(response);
    map = (Map<String, Object>) map.get("hits");
    List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("hits");
    for(Map<String, Object> singleMap : list) {
      ids.add((String) singleMap.get("_id"));
    }
    DbgUtil.logger().debug("SEARCHED IDs : " + ids);
    return ids;
  }
  
  protected String getSearchDocument(String input) {
    Map<String, Object> document = new HashMap<>();
    Map<String, Object> contents = new HashMap<>();
    Map<String, Object> multiMatch = new HashMap<>();
    Map<String, Object> must = new HashMap<>();
    Map<String, Object> bool = new HashMap<>();
    List<String> fields = new ArrayList<>();
    fields.add("title^3");
    fields.add("content");
    
    multiMatch.put("query", input);
    multiMatch.put("operator", "and");
    multiMatch.put("fields", fields);
    multiMatch.put("type", "cross_fields");
    contents.put("multi_match", multiMatch);
    
    must.put("must", contents);
    bool.put("bool", must);
    
    document.put("from", 0);
    document.put("size", 10);
    document.put("timeout", 3000);
    document.put("explain", false);
    document.put("query", bool);
    
    JSONObject jsonObject = new JSONObject(document);
    return jsonObject.toString();
  }
  
  protected File getIndexingFile(String title, String content, String id) {
    File jsonFile = getJsonFileByFileName(id);
    OutputStreamWriter osw;
    try {
      osw = new OutputStreamWriter(new FileOutputStream(jsonFile), "UTF-8");
      osw.write(getIndexingDocument(title, content, id));
      osw.flush();
      osw.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {}
    return jsonFile;
  }
  
  protected String getIndexingDocument(String title, String content, String id) {
    ArrayList<Object> list = new ArrayList<>();
    list.add(getIndexingSingleDocument(title, content, id));
    return list.toString();
  }
  
  protected JSONObject getIndexingSingleDocument(String title, String content, String id) {
    Map<String, Object> searchContents = new HashMap<>();
    searchContents.put("title", title);
    searchContents.put("content", content);
    searchContents.put("id", id);
    
    Map<String, Object> map = new HashMap<>();
    map.put("action", "add");
    map.put("id", id);
    map.put("fields", searchContents);
    JSONObject jsonObject = new JSONObject(map);
    return jsonObject;
  }
  
  protected File getJsonFileByFileName(String fileName) {
    File catalinaBase = new File(System.getProperty("catalina.base")).getAbsoluteFile();
    File propertyFile = new File(catalinaBase, "/documents/" + fileName + ".json");
    return propertyFile;
    
  }
}
