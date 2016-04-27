package com.toast.votree.controller.view;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
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
import com.toast.votree.oauthinfo.Payco;
import com.toast.votree.service.SessionRedisService;
import com.toast.votree.util.CookieUtil;
import com.toast.votree.util.DbgUtil;
import com.toast.votree.vo.User;

@Controller
public class PaycoController {
  @Autowired
  RestServer restServer;
  
  @Autowired
  RestTemplate restTemplate;
  @Autowired
  Payco payco;

  @Autowired
  SessionRedisService redisService;

  @RequestMapping(value = "/payco", method = RequestMethod.GET)
  public void paycoLoginStart(@RequestHeader("Referer") String referer, HttpServletRequest request,
      HttpServletResponse response) throws IOException {
    Cookie cookie = CookieUtil.getCookieByName(request.getCookies(), "VTSESSION");
    String loginUrlKey = "";
    if(cookie != null){
      loginUrlKey = "PAYCO_" + cookie.getValue();
    }
    redisService.opsHashForPutLoginUrl(loginUrlKey, "loginUrl", referer);
    String state = UUID.randomUUID().toString();
    state = URLEncoder.encode(state,"EUC-KR");
    redisService.opsHashForPutLoginUrl(loginUrlKey, "state", state);
    String paycoRedirectUri = payco.getPaycoAuthorizeUri(state);
    response.sendRedirect(paycoRedirectUri);
  }

  @SuppressWarnings("unchecked")
  @RequestMapping(value="/payco/auth", method = RequestMethod.GET)
  public String getPaycoUser(@RequestParam(value="code") String code,@RequestParam(value="state") String state ,HttpServletRequest request) throws IOException{
    /* Access Token 가져오기 */
    Cookie cookie = CookieUtil.getCookieByName(request.getCookies(), "VTSESSION");
    String loginUrlKey = "";
    if(cookie != null){
      loginUrlKey = "PAYCO_" + cookie.getValue();
    }
    String savedState = (String) redisService.findDataByKeyInHashKey(loginUrlKey, "state");
    if(!savedState.equals(state)) {
      DbgUtil.logger().error("state가 다르기 때문에 에러메시지 ");    //TODO errorPage!!!
    }
    DbgUtil.logger().debug(state);
    String paycoAccessTokenUri = payco.getPaycoAccessTokenUri(code,state);  
    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(paycoAccessTokenUri);
    Map<String,String> paycoAccessToken = restTemplate.getForObject(uriBuilder.build().toUri(), Map.class); 

    /* Payco 유저 정보 가져오기 */
    String paycoUserUri = payco.getPaycoUserUri();
    uriBuilder = UriComponentsBuilder.fromHttpUrl(paycoUserUri);
    
    Map<String, String> paramIdToken = new HashMap<String,String>();
    paramIdToken.put("client_id", payco.getPaycoClientId());
    paramIdToken.put("access_token", paycoAccessToken.get("access_token"));
    Map<String,String> paycoProviderKey= restTemplate.postForObject(
        uriBuilder.build().toUri()
        , paramIdToken
        , Map.class); 
    /* OauthRestController로 보내기 */
    final String REQUEST_URL = "api/v0.1/users";
    uriBuilder = UriComponentsBuilder
        .fromPath(REQUEST_URL)
        .scheme("http")
        .host(restServer.getHostName())
        .port(restServer.getHostPort())
        .queryParam("oauth_provider", "Payco")
        .queryParam("provider_key", paycoProviderKey.get("idNo"))
        .queryParam("name", URLEncoder.encode(makeRandomName(),"UTF-8" ));
        
    
    User currentUser = restTemplate.getForObject(uriBuilder.build().toUri(), User.class);

    redisService.opsHashForPutUser(cookie.getValue(), currentUser.getId(), currentUser.getName());
    String urlBeforeLogin = redisService.findDataByKeyInHashKey(loginUrlKey, "loginUrl").toString();
    redisService.delete(loginUrlKey);
   
    return "redirect:" + urlBeforeLogin ;
    
  }

  private String makeRandomName() {
    StringBuilder sb = new StringBuilder();
    sb.append(makeRandomAdjective());
    sb.append(" " + makeRandomNoun());
    return sb.toString();
  }

  private String makeRandomNoun() {
    Random generator = new Random();
    int index = generator.nextInt(31);
    String[] nouns = { "곤충", "메뚜기", "사자", "호랑이", "귀뚜라미", "매미", "개미", "헐크", "강아지", "고양이", "기린", "코끼리", "표범", "하마", "귀신",
        "개구리", "상어", "고래", "고등어", "멸치", "오징어", "문어", "소라", "홍합", "참치", "연어", "염소", "돼지", "비둘기", "독수리", "참새", "병아리",
        "오리", "꿩" };
    return nouns[index];
  }

  private String makeRandomAdjective() {
    Random generator = new Random();
    int index = generator.nextInt(29);
    String[] nouns = { "화난", "즐거운", "이쁜", "못생긴", "유명한", "대단한", "행복한", "민감한", "둔한", "뚱뚱한", "마른", "더러운", "깨끗한", "추운",
        "더운", "출근하는", "퇴근하는", "활동적인", "겁이많은", "겁이없는", "큰", "작은", "빨간", "금발의", "똒똑한", "멍청한", "건강한", "아픈", "평온한", "흥분한" };
    return nouns[index];
  }
}
