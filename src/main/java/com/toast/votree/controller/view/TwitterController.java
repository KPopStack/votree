package com.toast.votree.controller.view;


import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.toast.votree.config.RestServer;
import com.toast.votree.service.SessionRedisService;
import com.toast.votree.util.CookieUtil;
import com.toast.votree.util.DbgUtil;
import com.toast.votree.vo.User;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

@Controller
public class TwitterController {
  @Autowired
  RestServer restServer;
  @Autowired
  RestTemplate restTemplate;
  @Autowired
  TwitterFactory twitterFactory;
  
  @Autowired
  SessionRedisService redisService;
  
  @Autowired
  com.toast.votree.oauthinfo.Twitter twitterKeySecret;
  
  @RequestMapping(value = "/twitter", method = RequestMethod.GET)
  public void twitterLoginStart(@RequestHeader("Referer") String referer, HttpServletRequest request
      , HttpServletResponse response) throws IOException {
    Cookie cookie = CookieUtil.getCookieByName(request.getCookies(), "VTSESSION");
    String loginUrlKey = "";
    if(cookie != null){
      loginUrlKey = "Twitter_" + cookie.getValue();
    }
    redisService.opsHashForPutLoginUrl(loginUrlKey, "loginUrl", referer);
    
    Twitter twitter = twitterFactory.getInstance();
    twitter.setOAuthConsumer(twitterKeySecret.getConsumerKey(), twitterKeySecret.getConsumerSecret());
    RequestToken requestToken;
    try {
      requestToken = twitter.getOAuthRequestToken();
      redisService.createDataByKeyInHashKey(loginUrlKey, "twitterRequestToken", requestToken);
      String redirectUrl = requestToken.getAuthenticationURL();
      response.sendRedirect(redirectUrl);
    } catch (TwitterException e) {
      DbgUtil.logger().error(e.getMessage());
    }
  }

  @RequestMapping(value = "/twitter/auth", method = RequestMethod.GET)
  public String getTwitterUserInfo(HttpServletRequest request)
      throws IOException, IllegalStateException, TwitterException {
    Twitter twitter = new TwitterFactory().getInstance();
    twitter.setOAuthConsumer(twitterKeySecret.getConsumerKey(), twitterKeySecret.getConsumerSecret());
    String oauthVerifier = request.getParameter("oauth_verifier");
    Cookie cookie = CookieUtil.getCookieByName(request.getCookies(), "VTSESSION");
    String loginUrlKey = "";
    if(cookie !=null){
      loginUrlKey = "Twitter_" + cookie.getValue();
    }
    RequestToken requestToken = (RequestToken) redisService.findDataByKeyInHashKey(loginUrlKey, "twitterRequestToken");
    
    AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, oauthVerifier);
    twitter.setOAuthAccessToken(accessToken);
    
    final String REQUEST_URL = "api/v0.1/users";
    UriComponentsBuilder uriBuilder = UriComponentsBuilder
        .fromPath(REQUEST_URL)
        .scheme("http")
        .host(restServer.getHostName())
        .port(restServer.getHostPort())
        .queryParam("name", URLEncoder.encode(twitter.showUser(accessToken.getUserId()).getName(),"UTF-8" ))
        .queryParam("provider_key", accessToken.getUserId())
        .queryParam("oauth_provider", "Twitter");

    User currentUser =  restTemplate.getForObject(uriBuilder.build().toUri(), User.class );
    redisService.opsHashForPutUser(cookie.getValue(), currentUser.getId(), currentUser.getName());
    
    String urlBeforeLogin = redisService.findDataByKeyInHashKey(loginUrlKey, "loginUrl").toString();
    redisService.delete(loginUrlKey);
   
    return "redirect:" + urlBeforeLogin ;
  }
}
