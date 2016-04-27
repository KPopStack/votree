package com.toast.votree.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Service;

import com.toast.votree.util.DbgUtil;
import com.toast.votree.vo.User;

import net.minidev.json.JSONObject;
public class ToastSearchUser extends ToastSearch{

  public void indexingToToastSearch(User user) {
    sendRequestToToastSearch(getIndexingFile(user.getName(), user.getEmail(), String.valueOf(user.getId())));
  }
}
