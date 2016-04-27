package com.toast.votree.controller.view;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.toast.votree.config.RestServer;
import com.toast.votree.oauthinfo.Facebook;
import com.toast.votree.service.SessionRedisService;
import com.toast.votree.util.CookieUtil;
import com.toast.votree.util.DbgUtil;
import com.toast.votree.vo.User;

@Controller
public class FacebookController {
  @Autowired
  RestServer restServer;
  @Autowired
  Facebook facebook;
  @Autowired
  RestTemplate restTemplate;
  
  @Autowired
  SessionRedisService redisService;

  @RequestMapping(value = "/facebook", method = RequestMethod.GET)
  public void facebookLoginStart(@RequestHeader("Referer") String referer, HttpServletRequest request
      , HttpServletResponse response) throws IOException {
    Cookie cookie = CookieUtil.getCookieByName(request.getCookies(), "VTSESSION");
    String loginUrlKey = "";
    if(cookie != null){
      loginUrlKey= "Facebook_" + cookie.getValue();
    }
    redisService.opsHashForPutLoginUrl(loginUrlKey, "loginUrl", referer);
    String state = UUID.randomUUID().toString();  
    redisService.opsHashForPutLoginUrl(loginUrlKey, "state", state);
    String facebookAuthorizeUri = facebook.getFacebookAuthorizeUri(state);
    response.sendRedirect(facebookAuthorizeUri);
  }

  @SuppressWarnings("unchecked")
  @RequestMapping(value = "/facebook/auth", method = RequestMethod.GET)
  public String getFacebookUser(@RequestParam(value="code", defaultValue="") String code
      , @RequestParam(value="state") String state
      , HttpServletRequest request) throws SecurityException, IOException {
    Cookie cookie = CookieUtil.getCookieByName(request.getCookies(), "VTSESSION");
    String loginUrlKey = "";
    if(cookie != null){
      loginUrlKey = "Facebook_" + cookie.getValue();
    }
    String savedState = (String)redisService.findDataByKeyInHashKey(loginUrlKey, "state");

    if (code == null || code.length() == 0) {
      throw new RuntimeException("ERROR: Didn't get code parameter in callback.");
    }
    if(!savedState.equals(state)) {
      DbgUtil.logger().error("state가 다르기 때문에 에러메시지 ");    //TODO errorPage!!!
    }
    /* 지급받은 코드로 access token 생성  */
    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(facebook.getFacebookGraphUri(code));
    String accessToken = restTemplate.getForObject(uriBuilder.build().toUri(), String.class);
    accessToken = facebook.getProcessingAccessToken(accessToken);
    
    /* Facebook에서 유저 정보 가져오기 */
    uriBuilder = UriComponentsBuilder.fromHttpUrl(facebook.getFacebookUserUri(accessToken));
    Map<String,String> facebookUser = restTemplate.getForObject(uriBuilder.build().toUri(), Map.class);
   
    /* 가져온 유저 정보를 OauthRestController로 보내기 */
    final String REQUEST_URL = "api/v0.1/users";
    uriBuilder = UriComponentsBuilder
        .fromPath(REQUEST_URL)
        .scheme("http")
        .host(restServer.getHostName())
        .port(restServer.getHostPort())
        .queryParam("provider_key", facebookUser.get("id"))
        .queryParam("oauth_provider", "Facebook")
        .queryParam("name", URLEncoder.encode(facebookUser.get("name"),"UTF-8" ))
        .queryParam("email",facebookUser.get("email"));
    User currentUser = restTemplate.getForObject(uriBuilder.build().toUri(), User.class);
    redisService.opsHashForPutUser(cookie.getValue(), currentUser.getId(), currentUser.getName());

    String urlBeforeLogin = redisService.findDataByKeyInHashKey(loginUrlKey, "loginUrl").toString();
    redisService.delete(loginUrlKey);
  
    return "redirect:" + urlBeforeLogin;
  }

}