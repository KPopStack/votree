package com.toast.votree.test.interceptor;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.toast.votree.service.SessionRedisService;
import com.toast.votree.util.DbgUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
    "classpath:spring/applicationContext-test.xml",
    "classpath:spring/servlet-context.xml"
})
public class VotingInterceptorTest {

  @Autowired
  private RequestMappingHandlerMapping handlerMapping;

  @Autowired
  SessionRedisService redisService;

  Cookie cookie;
  MockHttpServletRequest request;
  MockHttpServletResponse response;
  @Before
  public void setup(){
    cookie = new Cookie("VTSESSION", "RANDOM_STRING");
    redisService.expire("RANDOM_STRING", 30, TimeUnit.SECONDS);
    
    request = new MockHttpServletRequest("GET","/voting/1/vote");
    response = new MockHttpServletResponse();
  }

  @Test
  public void votingInterceptTest_쿠키에_사용자정보_있는_경우() throws Exception {
    redisService.createDataByKeyInHashKey("RANDOM_STRING", "userName", "USERNAME");
    redisService.createDataByKeyInHashKey("RANDOM_STRING", "userId", "USERID");
    request.setCookies(cookie);
    
    HandlerExecutionChain handlerExecutionChain = handlerMapping.getHandler(request);
    HandlerInterceptor[] interceptors = handlerExecutionChain.getInterceptors();

    for(HandlerInterceptor interceptor : interceptors){
      assertEquals(true, interceptor.preHandle(request, response, handlerExecutionChain.getHandler()));
    }
    
    assertEquals(200, response.getStatus());
  }
  
  @Test
  public void votingInterceptTest_쿠키에_사용자정보_없는_경우() throws Exception {
    request.setCookies(cookie);
    HandlerExecutionChain handlerExecutionChain = handlerMapping.getHandler(request);
    HandlerInterceptor[] interceptors = handlerExecutionChain.getInterceptors();
    for(HandlerInterceptor interceptor : interceptors){
      interceptor.preHandle(request, response, handlerExecutionChain.getHandler());
    }
    assertEquals(302, response.getStatus());
  }
  
  @Test
  @Ignore
  public void votingInterceptTest_쿠키가_NULL인경우() throws Exception {
    HandlerExecutionChain handlerExecutionChain = handlerMapping.getHandler(request);
    HandlerInterceptor[] interceptors = handlerExecutionChain.getInterceptors();
    for(HandlerInterceptor interceptor : interceptors){
      assertEquals(true, interceptor.preHandle(request, response, handlerExecutionChain.getHandler()));
    }
    assertEquals(200, response.getStatus());
  }


  @After
  public void deleteRedisKey(){
    redisService.delete("RANDOM_STRING");
  }

}
