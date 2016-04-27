package com.toast.votree.interceptor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.CookieGenerator;

import com.toast.votree.manager.DbShardingManager;
import com.toast.votree.service.ResponseCheckRedisService;
import com.toast.votree.service.SessionRedisService;
import com.toast.votree.util.CookieUtil;
import com.toast.votree.util.DbgUtil;

public class CookieInterceptor extends HandlerInterceptorAdapter {
  
  @Autowired
  SessionRedisService redisService;
  @Autowired
  DbShardingManager dbShardingManager;
  @Autowired
  ResponseCheckRedisService responseCheckRedisService;
  
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    final int REDIS_EXPIRE_TIME = 12;
    Cookie cookie = CookieUtil.getCookieByName(request.getCookies(), "VTSESSION");
    if(cookie == null){
      CookieGenerator cookieGenerator = new CookieGenerator();  
      cookieGenerator.setCookieName("VTSESSION");
      cookieGenerator.addCookie(response, UUID.randomUUID().toString());
    }
    else if(cookie.getName().equals("VTSESSION")) {
      if(redisService.hasKey(cookie.getValue())) {
        request.setAttribute("userName", redisService.findDataByKeyInHashKey(cookie.getValue(), "userName"));
        request.setAttribute("userId", redisService.findDataByKeyInHashKey(cookie.getValue(), "userId"));
      }
      redisService.expire(cookie.getValue(), REDIS_EXPIRE_TIME, TimeUnit.HOURS);
    }
    long currentTime = System.currentTimeMillis();
    request.setAttribute("bTime", currentTime);
    return true;
  }
  
  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
      long currentTime = System.currentTimeMillis();
      long beginTime = (long) request.getAttribute("bTime");
      long processedTime = currentTime - beginTime;
      
      String requestUri = getSimpleRequestUri(request.getRequestURI());
      String inDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      Map<String, Object> map = new HashMap<>();
      map.put("date", inDate);
      map.put("responseTime", processedTime);
      if(!requestUri.equals("/check")) {
        responseCheckRedisService.createDataByKey(requestUri, map);
      }
  }
  private String getSimpleRequestUri(String uri) {
      if(!uri.equals("/")) {
          uri = uri.split("/")[1];
          uri = "/" + uri;
      }
      return uri;
  }
}
