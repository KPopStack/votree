package com.toast.votree.interceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.toast.votree.service.RedisService;
import com.toast.votree.service.SessionRedisService;
import com.toast.votree.util.CookieUtil;
import com.toast.votree.util.DbgUtil;

/*
 * 로그인 정보가 없다면 메인 화면으로 리다이렉트합니다.
 * 매퍼는 servlet-context.xml에 정의되어있습니다.(지금은 /voting/** 에서만 인터셉트합니다.)
 */
public class VotingInterceptor extends HandlerInterceptorAdapter{
  
  @Autowired
  SessionRedisService redisService;
  
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
    Cookie cookie = CookieUtil.getCookieByName(request.getCookies(), "VTSESSION");
    if(cookie != null){
      try {
        if(redisService.findDataByKeyInHashKey(cookie.getValue(), "userId") == null ){
          response.sendRedirect("/");  
          return false;
        }
      } catch (Exception e) {
        DbgUtil.logger().debug(e.getMessage());
      }
    }
    return true;
  }
}
