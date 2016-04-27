package com.toast.votree.test.controller.view;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

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
import org.springframework.data.redis.core.HashOperations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.toast.votree.config.RestServer;
import com.toast.votree.controller.view.PaycoController;
import com.toast.votree.oauthinfo.Payco;
import com.toast.votree.service.SessionRedisService;
import com.toast.votree.util.CookieUtil;
import com.toast.votree.vo.User;

@RunWith(PowerMockRunner.class)
@ContextConfiguration(locations = {
    "classpath:spring/applicationContext.xml",
    "classpath:spring/servlet-context.xml"
})
@PrepareForTest({Payco.class, UriComponentsBuilder.class, CookieUtil.class, RestTemplate.class})
public class PaycoControllerTest {
  
  @Mock
  MockHttpServletResponse mockResponse;
  @Mock
  MockHttpServletRequest mockRequest;
  @Mock
  RestServer restServer;
  @Mock
  Payco payco;
  @Mock
  SessionRedisService redisService;
  @Mock
  RestTemplate restTemplate;
  @Mock
  HashOperations<String, Object, Object> hashOperations;
  @InjectMocks
  PaycoController paycoController;
  Cookie cookie;
  UriComponentsBuilder uriBuilder;
  @Before
  public void setUp(){
    MockitoAnnotations.initMocks(this);
    mockRequest = new MockHttpServletRequest();
    cookie = new Cookie("cookie_name","cookie_value");
  }
  
  @Test
  public void paycoLoginStartTest() throws IOException{
    mockStatic(CookieUtil.class);
    mockStatic(Payco.class);
    PowerMockito.when(CookieUtil.getCookieByName(anyObject(), anyString())).thenReturn(cookie);
    Mockito.when(redisService.expire(anyString(), anyLong(), anyObject())).thenReturn(true);
    Mockito.doNothing().when(redisService).opsHashForPutLoginUrl(anyString(), anyString(), anyString());
    PowerMockito.when(payco.getPaycoAuthorizeUri(anyString())).thenReturn("/url");
    paycoController.paycoLoginStart("url", mockRequest, mockResponse);
  }
  
  @Test
  public void getPaycoUserTest() throws IOException{
    uriBuilder = UriComponentsBuilder.fromHttpUrl("http://alpha-id-bo.payco.com:10003/neid_bo/oauth/getUserStatByToken");
    Map<String, String> paycoAccessToken = new HashMap<>();
    paycoAccessToken.put("access_token", "accessToken1234");
    Map<String, String> paycoProviderKey = new HashMap<>();
    paycoProviderKey.put("idNo", "1234");
    User user = new User();
    user.setName("SeongWonKong");
    user.setId(1234);
    mockStatic(Payco.class);
    mockStatic(UriComponentsBuilder.class);
    mockStatic(CookieUtil.class);
    PowerMockito.when(CookieUtil.getCookieByName(anyObject(), anyString())).thenReturn(cookie);
    
    PowerMockito.when(payco.getPaycoAccessTokenUri("code1234","Random()()()")).thenReturn("accessToken1234");
    PowerMockito.when(UriComponentsBuilder.fromHttpUrl(anyString())).thenReturn(uriBuilder);
    Mockito.when(restTemplate.getForObject(Mockito.any(URI.class),eq(Map.class))).thenReturn(paycoAccessToken);
    PowerMockito.when(payco.getPaycoUserUri()).thenReturn("testUri");
    PowerMockito.when(restTemplate.postForObject(Mockito.any(URI.class), anyObject(), eq(Map.class))).thenReturn(paycoProviderKey);
    PowerMockito.when(UriComponentsBuilder
        .fromPath(anyString())).thenReturn(uriBuilder);
    PowerMockito.when(restTemplate.getForObject(Mockito.any(URI.class), eq(User.class))).thenReturn(user);

    Mockito.when(redisService.opsForHash()).thenReturn(hashOperations);
    PowerMockito.when(redisService.findDataByKeyInHashKey(anyString(), anyString())).thenReturn("/url");
    Mockito.doNothing().when(redisService).delete(anyString());
    
    String result = paycoController.getPaycoUser("1234","ffffff",mockRequest);
    assertThat(result,is("redirect:/url"));
  }
  
}
