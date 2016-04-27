package com.toast.votree.controller.view;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.toast.votree.config.RestServer;
import com.toast.votree.config.ToastCloud;
import com.toast.votree.exception.VotreeIsCompleteException;
import com.toast.votree.service.SessionRedisService;
import com.toast.votree.util.CookieUtil;
import com.toast.votree.util.JsonUtil;

@Controller
public class VotingController {

  @Autowired
  RestServer restServer;
  
  @Autowired
  RestTemplate restTemplate;
  
  @Autowired
  ToastCloud toastCloud;
  
  @Autowired
  SessionRedisService redisService;
  
  @RequestMapping(value = "/voting/{votreeId}/{voteMode}", method = RequestMethod.GET)
  public String displayVotingPage(@PathVariable("votreeId") String votreeId,
      @PathVariable("voteMode") String voteMode, HttpServletRequest request, Model model) {
    //요청 URI
    final String REQUEST_PATH = "api/v0.1/votrees/{votreeId}/votes";
    int userId = -1;
    Cookie cookie = CookieUtil.getCookieByName(request.getCookies(), "VTSESSION");
    if(cookie != null){
      userId = (int) redisService.findDataByKeyInHashKey(cookie.getValue(), "userId");
    }
    //URI ComponetsBuilder 생성
    UriComponentsBuilder uriBuilder = UriComponentsBuilder
        .fromPath(REQUEST_PATH)
        .scheme("http")
        .host(restServer.getHostName())
        .port(restServer.getHostPort());
    //요청 헤더 설정
    HttpHeaders headers = new HttpHeaders();
    headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.set("userId", String.valueOf(userId));
    //요청시 사용할 HttpEntity를 생성
    
    HttpEntity<String> entity = new HttpEntity<String>(headers);

    //PathVarialbe 설정
    Map<String, String> pathVariables = new HashMap<>();
    pathVariables.put("votreeId", votreeId);
    
    //RestTemplate 를 사용하여 HTTP "apRequest하고 응답으로 ResponseEntity를 리턴받음
    ResponseEntity<String> response = restTemplate.exchange(uriBuilder.build().toUriString(), HttpMethod.GET, entity, String.class, pathVariables);
    Map<String, Object> map = JsonUtil.jsonToMap(response.getBody());
    if (map.get("error") != null) {
      throw new VotreeIsCompleteException("완료된 Votree의 투표화면에는 접근할 수 없습니다.");
    }
    
    model.addAllAttributes(map);
    model.addAttribute("voteMode", voteMode);
    
    final String FILE_CONTROLLER_PATH = "/files/";
    model.addAttribute("fileStorageControllerUrl", FILE_CONTROLLER_PATH);
    
    return "voting";
  }
}
