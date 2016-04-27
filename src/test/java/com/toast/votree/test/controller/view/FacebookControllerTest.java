package com.toast.votree.test.controller.view;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


import static org.powermock.api.mockito.PowerMockito.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;

import com.toast.votree.config.RestServer;
import com.toast.votree.controller.view.FacebookController;
import com.toast.votree.oauthinfo.Facebook;
import com.toast.votree.service.SessionRedisService;
import com.toast.votree.util.CookieUtil;
import com.toast.votree.vo.User;

@RunWith(PowerMockRunner.class)
@ContextConfiguration(locations = {
    "classpath:spring/applicationContext-test.xml",
    "classpath:spring/servlet-context.xml"
})
@PrepareForTest({Facebook.class, UriComponentsBuilder.class, CookieUtil.class, RestTemplate.class})
public class FacebookControllerTest {
  @Mock
  RestServer restServer;
  @Mock
  RestTemplate restTemplate;
  @Mock
  MockHttpServletResponse mockResponse;
  @Mock
  MockHttpServletRequest mockRequest;
  @Mock
  SessionRedisService redisService;
  @Mock
  HashOperations<String,Object,Object> hashOperations;
  @Mock
  Facebook facebook;
  
  @InjectMocks
  FacebookController facebookController;
  
  UriComponentsBuilder uriBuilder;
  String facebookUserUri;
  String accessToken;
  Map<String, String> facebookUser;
  User user;
  Cookie cookie;

  @Before
  public void setUp(){
   MockitoAnnotations.initMocks(this);
   mockRequest = new MockHttpServletRequest();
   facebookUser = new HashMap<>();
   facebookUser.put("id", "facebookUniqueId1234");
   facebookUser.put("name", "SeongWonKong");
   facebookUser.put("email", "seongwon.kong@nhnent.com");
   user = new User();
   user.setId(111);
   user.setName("SeongWonKong");
   cookie = new Cookie("a", "b");
   accessToken = "atat1234";
   facebookUserUri = "https://graph.facebook.com/v2.5/me?"
       + "fields=id,name,email"
       + "&access_token="+accessToken;
   uriBuilder = UriComponentsBuilder.fromHttpUrl(facebookUserUri);
  }
  
  @Test
  public void getFacebookUserTest() throws Exception {
    mockRequest.addParameter("code", "code1234");
    mockStatic(Facebook.class);
    mockStatic(UriComponentsBuilder.class);
    mockStatic(CookieUtil.class);
    PowerMockito.when(facebook.getProcessingAccessToken(anyString())).thenReturn(accessToken);
    PowerMockito.when(facebook.getFacebookUserUri(anyString())).thenReturn(facebookUserUri);
    PowerMockito.when(UriComponentsBuilder.fromHttpUrl(anyString())).thenReturn(uriBuilder);
    PowerMockito.when(UriComponentsBuilder
        .fromPath(anyString())).thenReturn(uriBuilder);
    PowerMockito.when(CookieUtil.getCookieByName(anyObject(), anyString())).thenReturn(cookie);
    PowerMockito.when(restTemplate.getForObject(Mockito.any(URI.class), eq(Map.class))).thenReturn(facebookUser);
    PowerMockito.when(restTemplate.getForObject(Mockito.any(URI.class), eq(User.class))).thenReturn(user);
    
    Mockito.when(redisService.opsForHash()).thenReturn(hashOperations);
    
    PowerMockito.when(redisService.findDataByKeyInHashKey(anyString(), anyString())).thenReturn("/url");
    Mockito.doNothing().when(redisService).delete(anyString());
    
    String result = facebookController.getFacebookUser("code1234","state1234",mockRequest);
    assertThat(result,is("redirect:/url"));
  }
  
  @Test(expected=RuntimeException.class)
  public void getFacebookUserTest_code가_empty일_경우() throws Exception {
    mockRequest.addParameter("code", "");
    facebookController.getFacebookUser("code1234","state1234",mockRequest);
  }
  
  @Test(expected=RuntimeException.class)
  public void getFacebookUserTest_code가_null일_경우() throws Exception {
    facebookController.getFacebookUser("code1234","state1234",mockRequest);
  }
  
  @Test
  public void facebookLoginStartTest() throws IOException {
    String facebookAuthorizeUri = "/authorize/1234";
    String referer = "/urlBefore";
    mockStatic(CookieUtil.class);
    mockStatic(Facebook.class);
    
    PowerMockito.when(CookieUtil.getCookieByName(anyObject(), anyString())).thenReturn(cookie);
    Mockito.when(redisService.expire(anyString(),anyLong(),anyObject())).thenReturn(true);
    Mockito.doNothing().when(redisService).opsHashForPutLoginUrl(anyString(), anyString(), anyString());
    PowerMockito.when(facebook.getFacebookAuthorizeUri("state1234")).thenReturn(facebookAuthorizeUri);
    Mockito.doNothing().when(mockResponse).sendRedirect(anyString());
    facebookController.facebookLoginStart(referer, mockRequest, mockResponse);
  }
  
  @Test(expected=IOException.class)
  public void facebookLoginStartTest_sendRedirect가_IOException을_내는_경우() throws IOException {
    String facebookAuthorizeUri = null;
    String referer = "/urlBefore";
    mockStatic(CookieUtil.class);
    mockStatic(Facebook.class);
    PowerMockito.when(CookieUtil.getCookieByName(anyObject(), anyString())).thenReturn(cookie);
    Mockito.when(redisService.expire(anyString(),anyLong(),anyObject())).thenReturn(true);
    Mockito.when(redisService.opsForHash()).thenReturn(hashOperations);
    PowerMockito.when(facebook.getFacebookAuthorizeUri("state1234")).thenReturn(facebookAuthorizeUri);
    Mockito.doNothing().when(mockResponse).sendRedirect(anyString());
    Mockito.doThrow(IOException.class).when(mockResponse).sendRedirect(anyString());
    facebookController.facebookLoginStart(referer, mockRequest, mockResponse);
  }
}
