package com.toast.votree.test.service;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.toast.votree.manager.DbShardingManager;
import com.toast.votree.service.DatabaseMapperRedisService;
import com.toast.votree.service.ToastSearchUser;
import com.toast.votree.service.UserServiceImpl;
import com.toast.votree.sharding.UserMapper;
import com.toast.votree.util.RestResponse;
import com.toast.votree.vo.User;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
    "classpath:spring/applicationContext-test.xml",
    "classpath:spring/servlet-context.xml"
})
@Transactional
public class UserServiceTest {
  @Mock
  DatabaseMapperRedisService databaseMapperRedisService;
  @Mock
  DbShardingManager dbShardingManager;
  @Mock
  ToastSearchUser toastSearchUser;
  @InjectMocks
  UserServiceImpl userServiceImpl;
  
  @Autowired
  SqlSessionTemplate sqlSessionTemplate_common;
  
  User normalUser;
  List<User> users;
  UserMapper userMapper;
  
  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    users = new ArrayList<>();
    normalUser = new User();
    normalUser.setId(1)
      .setEmail("normalUser@votree.com")
      .setName("normalName")
      .setOauthProvider("normalProvider")
      .setProviderKey("normalUserProviderKey");
    
    userMapper = mock(UserMapper.class);
    users.add(normalUser);
    userMapper.insertUser(normalUser);
    
    normalUser = new User();
    normalUser.setId(2)
      .setEmail("normalUser2@votree.com")
      .setName("normalName2")
      .setOauthProvider("normalProvider2")
      .setProviderKey("normalUserProviderKey2");
    
    users.add(normalUser);
    userMapper.insertUser(normalUser);
    
    normalUser = new User();
    normalUser.setId(3)
      .setEmail("normalUser3@votree.com")
      .setName("normalName3")
      .setOauthProvider("normalProvider3")
      .setProviderKey("normalUserProviderKey3");
    
    users.add(normalUser);
    userMapper.insertUser(normalUser);
    
    when(dbShardingManager.getMapperInCommonDb(anyObject())).thenReturn(userMapper);
    
  }

  
  @Test
  public void updateUserTest(){
    String newEmail = "m_normalUser2@votree.com";
    String newName = "normalName2";
    User updateUser = userServiceImpl.updateUser(2, newName, newEmail);
    
    assertThat(updateUser.getEmail(), is(newEmail));
    assertThat(updateUser.getName(), is(newName));
  }
  
  @Test
  public void findOrCreateUserByOauthTest_사용자존재() {
    when(userServiceImpl.findUserByProviderKey(anyString()))
      .thenReturn(normalUser);
    Mockito.doNothing().when(toastSearchUser).indexingToToastSearch(anyObject());
    User existUser = userServiceImpl.findOrSaveUserByOauth(normalUser.getName(), normalUser.getEmail(), normalUser.getOauthProvider(), normalUser.getProviderKey());
    assertThat(existUser.getEmail(), is(normalUser.getEmail()));
    assertThat(existUser.getName(), is(normalUser.getName()));
    assertThat(existUser.getOauthProvider(), is(normalUser.getOauthProvider()));
    assertThat(existUser.getProviderKey(), is(normalUser.getProviderKey()));
  }
  
  
  @Test
  public void createUserTest() {
    when(userMapper.insertUser(anyObject())).thenReturn(1);
    when(userMapper.selectUserByProviderKey(anyString())).thenReturn(normalUser);
    
    Mockito.doNothing().when(databaseMapperRedisService).createDataByKeyInHashKey(anyString(), anyString(), anyInt());
    
    User nm = userServiceImpl.createUser("normalName3", "normalUser3@votree.com", "normalProvider3", "normalUserProviderKey3");
    assertThat(nm.getName(), is("normalName3"));
    assertThat(nm.getEmail(), is("normalUser3@votree.com"));
    assertThat(nm.getOauthProvider(), is("normalProvider3"));
    assertThat(nm.getProviderKey(), is("normalUserProviderKey3"));
  }
  
  @Test
  public void modifyUserProfileTest_올바른값이_입력된_경우(){
    Map<String,String> jsonBody = new HashMap<>();
    jsonBody.put("name", "normalName4");
    jsonBody.put("email", "m_normalUser4@votree.com");
    
    RestResponse resultResponse = userServiceImpl.modifyUserProfile(1, jsonBody.get("name"), jsonBody.get("email"));
    assertTrue(resultResponse.getHeader().getIsSuccessful());
    assertThat(resultResponse.getHeader().getResultCode(),is(200));
    assertThat(resultResponse.getHeader().getResultMessage(),is("프로필 변경 성공"));
  }

  @Test
  public void modifyUserProfileTest_바디가_null인_경우(){
    Map<String,String> jsonBody = new HashMap<>();
    RestResponse resultResponse = userServiceImpl.modifyUserProfile(1, jsonBody.get("name"), jsonBody.get("email"));
    assertFalse(resultResponse.getHeader().getIsSuccessful());
    assertThat(resultResponse.getHeader().getResultCode(),is(400));
    assertThat(resultResponse.getHeader().getResultMessage(),is("이름 또는 이메일이 올바르지 않습니다."));
  }
  
  @Test
  public void findUserByIdTest(){
    User expectedUser = users.get(1);
    when(userMapper.selectUserById(expectedUser.getId())).thenReturn(expectedUser);
    User user = userServiceImpl.findUserById(2);
    assertThat(user.getEmail(), is(expectedUser.getEmail()));
    assertThat(user.getName(), is(expectedUser.getName()));
    assertThat(user.getOauthProvider(), is(expectedUser.getOauthProvider()));
    assertThat(user.getProviderKey(), is(expectedUser.getProviderKey()));
  }
  
  @Test
  public void findUsersByNameOrEmailTest(){
    List<User> expectedUsers = new ArrayList<>();
    expectedUsers.add(users.get(0));
    expectedUsers.add(users.get(1));
    List<String> expectedUserIds = new ArrayList<>();
    expectedUserIds.add(String.valueOf(users.get(0).getId()));
    expectedUserIds.add(String.valueOf(users.get(1).getId()));
    
    when(toastSearchUser.getIdsByInput(anyString())).thenReturn(expectedUserIds);
    when(userMapper.selectUserById(users.get(0).getId())).thenReturn(users.get(0));
    
    List<User> findUsers = userServiceImpl.findUsersByNameOrEmail(users.get(0).getEmail());
    
    assertThat(findUsers.get(0).getEmail(), is(expectedUsers.get(0).getEmail()));
    assertThat(findUsers.get(0).getName(), is(expectedUsers.get(0).getName()));
    assertThat(findUsers.get(0).getOauthProvider(), is(expectedUsers.get(0).getOauthProvider()));
    assertThat(findUsers.get(0).getProviderKey(), is(expectedUsers.get(0).getProviderKey()));
    
  }
  
}
