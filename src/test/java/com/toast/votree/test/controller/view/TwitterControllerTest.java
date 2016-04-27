package com.toast.votree.test.controller.view;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.io.IOException;

import javax.servlet.http.Cookie;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.toast.votree.config.RestServer;
import com.toast.votree.controller.view.TwitterController;
import com.toast.votree.service.SessionRedisService;
import com.toast.votree.util.CookieUtil;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

@RunWith(PowerMockRunner.class)
@ContextConfiguration(locations = {
    "classpath:spring/applicationContext-test.xml",
    "classpath:spring/servlet-context.xml"
})
@PrepareForTest({TwitterFactory.class, UriComponentsBuilder.class, CookieUtil.class, RestTemplate.class})
public class TwitterControllerTest {
  @Mock
  MockHttpServletResponse response;
  @Mock
  MockHttpServletRequest request;
  @Mock
  RestServer restServer;
  @Mock
  SessionRedisService redisService;
  @Mock
  RestTemplate restTemplate;
  @Mock
  HashOperations<String, Object, Object> hashOperations;
  @Mock
  TwitterFactory twitterFactory;
  @Mock
  Twitter twitter;
  @Mock
  com.toast.votree.oauthinfo.Twitter twitterKeySecret;
  @Mock
  AccessToken accessToken;
  @InjectMocks
  TwitterController twitterController;
  
  Cookie cookie;
  UriComponentsBuilder uriBuilder;
  @Before
  public void setUp(){
    MockitoAnnotations.initMocks(this);
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    cookie = new Cookie("cookie_name","cookie_value");
  }
  
  @Test
  public void twitterLoginStart() throws IOException, TwitterException {
    RequestToken requestToken = new RequestToken("123","3456");
    mockStatic(CookieUtil.class);
    mockStatic(TwitterFactory.class);
    PowerMockito.when(CookieUtil.getCookieByName(anyObject(), anyString())).thenReturn(cookie);
    Mockito.when(redisService.expire(anyString(), anyLong(), anyObject())).thenReturn(true);
    Mockito.when(redisService.findDataByKeyInHashKey(anyString(), anyString())).thenReturn(hashOperations);
    Mockito.when(twitterFactory.getInstance()).thenReturn(twitter);
    Mockito.when(twitter.getOAuthRequestToken()).thenReturn(requestToken);
    twitterController.twitterLoginStart("/url", request, response);
  }
  
  @Test(expected=IllegalStateException.class)
  public void getTwitterUserInfoTest() throws IllegalStateException, IOException, TwitterException {
    Mockito.when(twitterKeySecret.getConsumerKey()).thenReturn("abc");
    Mockito.when(twitterKeySecret.getConsumerSecret()).thenReturn("123");
    mockStatic(CookieUtil.class);
    PowerMockito.when(CookieUtil.getCookieByName(anyObject(), anyString())).thenReturn(cookie);
    //Mockito.when(redisService.findDataByKeyInHashKey(anyString(), anyString())).thenReturn(requestToken);
    //Mockito.when(twitter.getOAuthAccessToken(Mockito.any(RequestToken.class), anyString())).thenReturn(null);
    //Mockito.doNothing().when(twitter).setOAuthAccessToken(anyObject());
    
    
    twitterController.getTwitterUserInfo(request);
  }
  
}
