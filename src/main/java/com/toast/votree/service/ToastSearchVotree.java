package com.toast.votree.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.toast.votree.util.DbgUtil;
import com.toast.votree.vo.User;
import com.toast.votree.vo.Votree;

import net.minidev.json.JSONObject;
public class ToastSearchVotree extends ToastSearch{

  public void indexingToToastSearch(List<Votree> votrees) {
    sendRequestToToastSearch(getIndexingFile(votrees));
  }

  public void indexingToToastSearch(Votree votree) {
    sendRequestToToastSearch(getIndexingFile(votree.getTitle(), votree.getProposerName(), votree.getId()));
  }
  
  protected File getIndexingFile(List<Votree> votrees) {
    File jsonFile = getJsonFileByFileName(votrees.get(0).getId());
    OutputStreamWriter osw;
    try {
      osw = new OutputStreamWriter(new FileOutputStream(jsonFile), "UTF-8");
      osw.write(getIndexingDocument(votrees));
      osw.flush();
      osw.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {}
    return jsonFile;
  }
  
  protected String getIndexingDocument(List<Votree> votrees) {
    ArrayList<Object> list = new ArrayList<>();
    for(Votree votree : votrees) {
      list.add(getIndexingSingleDocument(votree.getTitle(), votree.getProposerName(), votree.getId()));
    }
    return list.toString();
  }
  
}
