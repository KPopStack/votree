package com.toast.votree.test.interceptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.toast.votree.service.SessionRedisService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
    "classpath:spring/applicationContext-test.xml",
    "classpath:spring/servlet-context.xml"
})
public class CookieInterceptorTest {
  @Autowired
  private RequestMappingHandlerMapping handlerMapping;

  @Autowired
  SessionRedisService redisService;

  Cookie cookie;

  @Before
  public void setup(){
    cookie = new Cookie("VTSESSION", "RANDOM_STRING");
    redisService.expire("RANDOM_STRING", 30, TimeUnit.SECONDS);
    redisService.createDataByKeyInHashKey("RANDOM_STRING", "userName", "USERNAME");
    redisService.createDataByKeyInHashKey("RANDOM_STRING", "userId", "USERID");
  }

  @Test
  @Ignore
  public void cookieInterceptorTest_쿠키가_없을때() throws Exception{
    MockHttpServletRequest request = new MockHttpServletRequest("GET","/");
    MockHttpServletResponse response = new MockHttpServletResponse();

    HandlerExecutionChain handlerExecutionChain = handlerMapping.getHandler(request);
    HandlerInterceptor[] interceptors = handlerExecutionChain.getInterceptors();

    for(HandlerInterceptor interceptor : interceptors){
      interceptor.preHandle(request, response, handlerExecutionChain.getHandler());
    }

    assertEquals(200,response.getStatus());
    assertNotNull(response.getCookie("VTSESSION"));
  }

  @Test
  public void cookieInterceptorTest_쿠키가_있을때() throws Exception{
    MockHttpServletRequest request = new MockHttpServletRequest("GET","/");
    MockHttpServletResponse response = new MockHttpServletResponse();
    request.setCookies(cookie);
    HandlerExecutionChain handlerExecutionChain = handlerMapping.getHandler(request);
    HandlerInterceptor[] interceptors = handlerExecutionChain.getInterceptors();

    for(HandlerInterceptor interceptor : interceptors){
      interceptor.preHandle(request, response, handlerExecutionChain.getHandler());
    }
    assertEquals(200,response.getStatus());
  }
  @After
  public void deleteRedisKey(){
    redisService.delete("RANDOM_STRING");
  }

}
