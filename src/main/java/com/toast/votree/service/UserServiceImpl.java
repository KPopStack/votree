package com.toast.votree.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.toast.votree.exception.NotExistUserException;
import com.toast.votree.manager.DbShardingManager;
import com.toast.votree.sharding.UserMapper;
import com.toast.votree.sharding.VotreeMapper;
import com.toast.votree.util.DbgUtil;
import com.toast.votree.util.RestResponse;
import com.toast.votree.vo.User;
import com.toast.votree.vo.Votree;

import net.minidev.json.JSONObject;

@Service("userService")
public class UserServiceImpl implements UserService {
  
  @Autowired
  DatabaseMapperRedisService databaseMapperRedisService;

  @Autowired
  DbShardingManager dbShardingManager;
  @Autowired
  ToastSearchUser toastSearchUser;
  @Autowired
  ToastSearchVotree toastSearchVotree; 
  public User findOrSaveUserByOauth(String name, String email, String oauthProvider, String providerKey) {
    User user = findUserByProviderKey(providerKey);
    if(user == null){
      user = createUser(name, email, oauthProvider, providerKey);
      toastSearchUser.indexingToToastSearch(user);
    }
    return user;
  }
  
  public RestResponse modifyUserProfile(int userId, String name, String email) {
    if(name == null || email == null){
      return  new RestResponse.Builder(false, 400, "이름 또는 이메일이 올바르지 않습니다.").build();
    }
    User user = updateUser(userId, name, email);
    return new RestResponse.Builder("프로필 변경 성공").body(user.getName()).build();
  }
  public User createUser(String name, String email, String oauthProvider, String providerKey) {
    final String USER_DB_INDEX = "0";
    User newUser = new User()
        .setName(name)
        .setEmail(email)
        .setOauthProvider(oauthProvider)
        .setProviderKey(providerKey);
    
    UserMapper userMapper = dbShardingManager.getMapperInCommonDb(UserMapper.class);
    userMapper.insertUser(newUser);
    databaseMapperRedisService.createDataByKeyInHashKey(String.valueOf(newUser.getId()), "dbIndex", USER_DB_INDEX);
    return newUser;
  }
  
  public User updateUser(int userId, String name, String email) {
    User modifiedUser = new User();
    modifiedUser.setId(userId)
      .setEmail(email)
      .setName(name);
    UserMapper userMapper = dbShardingManager.getMapperInCommonDb(UserMapper.class);
    userMapper.updateUser(modifiedUser);
    
    toastSearchUser.indexingToToastSearch(modifiedUser); // User Indexing Update

    /*
     * Votree Indexing Update
     */
    List<Votree> list = new ArrayList<>();
    for(VotreeMapper votreeMapper : dbShardingManager.getMappers(VotreeMapper.class)) {
      List<Votree> votrees = votreeMapper.selectVotreesByUserId(userId);
      for(Votree votree : votrees) {
        if(votree != null) {
          votree.setProposerName(name);
          list.add(votree);
        } 
      }
    }
    if(list.size() != 0 ) {
      toastSearchVotree.indexingToToastSearch(list);
    }
    return modifiedUser;
  }
  public User findUserByProviderKey(String providerKey){
    UserMapper userMapper = dbShardingManager.getMapperInCommonDb(UserMapper.class);
    return userMapper.selectUserByProviderKey(providerKey);
  }
  
  public User findUserById(int id) {
    UserMapper userMapper = dbShardingManager.getMapperInCommonDb(UserMapper.class);
    return userMapper.selectUserById(id);
  }
  
  public List<User> findUsersByNameOrEmail(String nameOrEmail) {
    UserMapper userMapper = dbShardingManager.getMapperInCommonDb(UserMapper.class);
    List<User> users = new ArrayList<>();
    List<String> list = toastSearchUser.getIdsByInput(nameOrEmail);
    for(String userId : list) {
      User user = userMapper.selectUserById(Integer.valueOf(userId));
      if(user != null) {
        users.add(user);
      }
    }
    return users;
  }

}
