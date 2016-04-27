package com.toast.votree.controller.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.toast.votree.config.RestServer;
import com.toast.votree.config.ToastCloud;
import com.toast.votree.exception.NotExistVotreeException;
import com.toast.votree.service.SessionRedisService;
import com.toast.votree.service.VotreeService;
import com.toast.votree.util.CookieUtil;
import com.toast.votree.util.DbgUtil;
import com.toast.votree.util.JsonUtil;
import com.toast.votree.vo.User;
import com.toast.votree.vo.Votree;

@Controller
public class VotreeResultController {
  @Autowired
  RestServer restServer;
  @Autowired
  ToastCloud toastCloud;
  @Autowired
  RestTemplate restTemplate;
  @Autowired
  VotreeService votreeService;
  @Autowired
  SessionRedisService redisService;
  
  @SuppressWarnings("unchecked")
  @RequestMapping(value = "/result/{votree_id}", method = RequestMethod.GET)
  public String getVotreeResult(@PathVariable(value = "votree_id") String votreeId, HttpServletRequest request, Model model) throws IOException {

    Cookie cookie = CookieUtil.getCookieByName(request.getCookies(), "VTSESSION");
    Object obj = null;
    if(cookie != null){
      obj = redisService.findDataByKeyInHashKey(cookie.getValue(), "userId");
    }
    int loginUserId = (obj == null) ? 0 : (int) obj;
    
    /* vote 테이블에서 top, personal 결과 자료를 가져옵니다. */
    final String REQUEST_URL_FOR_TOTAL_RESULT = "api/v0.1/votrees/" + votreeId + "/result";
    UriComponentsBuilder uriBuilder = UriComponentsBuilder
        .fromPath(REQUEST_URL_FOR_TOTAL_RESULT)
        .scheme("http")
        .host(restServer.getHostName())
        .port(restServer.getHostPort())
        .queryParam("userId", loginUserId);

    Map<String, Object> result = restTemplate.getForObject(uriBuilder.build().toUri(), Map.class);
    model.addAttribute("topResults", result.get("topResults"));
    model.addAttribute("sameChoiceUsers", result.get("sameChoiceUsers"));
    model.addAttribute("totalResults", result.get("totalResults"));
    model.addAttribute("jsonString_top", JsonUtil.toJsonString(result.get("topResults")));
    model.addAttribute("jsonString_total", JsonUtil.toJsonString(result.get("totalResults")));
    model.addAttribute("textItemOnly", result.get("textItemOnly"));
    List<Map<String, Object>> bestResults = votreeService.calcVotreeResultForBestChoice(votreeId);
    DbgUtil.logger().debug("TEST : " + bestResults);
    model.addAttribute("bestResults", bestResults);
    
    /* votree 정보를 가져옵니다. */
    final String REQUEST_URL_FOR_VOTREE = "api/v0.1/votrees/" + votreeId;
    uriBuilder = UriComponentsBuilder.fromPath(REQUEST_URL_FOR_VOTREE)
        .scheme("http")
        .host(restServer.getHostName())
        .port(restServer.getHostPort())
        .queryParam("userId", loginUserId)
        .queryParam("fromResult", "RESULT");     
    Votree votree = restTemplate.getForObject(uriBuilder.build().toUri(), Votree.class);
    int proposerId;
    try{
      proposerId = votree.getProposerId(); 
    }catch(NullPointerException e){
      throw new NotExistVotreeException(e, loginUserId);
    }
    
    /*
     * UserRestController에서 발의자 정보를 가져옵니다.
     */
    final String REQUET_URL_FOR_USER = "api/v0.1/users/" + proposerId;
    uriBuilder = UriComponentsBuilder.fromPath(REQUET_URL_FOR_USER)
        .scheme("http")
        .host(restServer.getHostName())
        .port(restServer.getHostPort());
    User proposer = restTemplate.getForObject(uriBuilder.build().toUri(), User.class);
    
    model.addAttribute("votree",votree);
    model.addAttribute("proposer",proposer);
    final String REQUEST_PATH = "/files/";
    uriBuilder = UriComponentsBuilder.fromPath(REQUEST_PATH)
      .scheme("http")
      .host(restServer.getHostName())
      .port(restServer.getHostPort());
    model.addAttribute("fileStorageControllerUrl",uriBuilder.build().toString());
    return "result";
  }
}
