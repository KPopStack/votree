package com.toast.votree.controller.view;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.toast.votree.config.RestServer;
import com.toast.votree.exception.NotExistVotreeException;
import com.toast.votree.service.DatabaseMapperRedisService;
import com.toast.votree.service.SessionRedisService;
import com.toast.votree.util.CookieUtil;
import com.toast.votree.util.DbgUtil;
import com.toast.votree.vo.User;
import com.toast.votree.vo.Vote;
import com.toast.votree.vo.Votree;

@Controller
public class DetailVotreeController {
  @Autowired
  RestServer restServer;
  @Autowired
  RestTemplate restTemplate;
  @Autowired
  SessionRedisService redisService;
  @Autowired
  DatabaseMapperRedisService databaseMapperRedisService;
  
  @SuppressWarnings("unchecked")
  @RequestMapping(value = "/detail/{votreeId}", method = {RequestMethod.GET, RequestMethod.POST })
  public String showVotreeDetail(HttpServletRequest request
      , @PathVariable(value = "votreeId") String votreeId
      , @RequestParam(value = "votreeHiddenInput", defaultValue = "") String votreePassword
      ,  Model model) throws NotExistVotreeException {
    Cookie cookie = CookieUtil.getCookieByName(request.getCookies(), "VTSESSION");
    Object obj = null;
    if(cookie != null){
      obj = redisService.findDataByKeyInHashKey(cookie.getValue(), "userId");
    }
    int loginUserId = (obj == null) ? 0:(int) obj; 
    
    /*
     * DetailVotreeRestController에서 하나의 보트리를 가져옵니다.
     */
    final String REQUEST_URL_FOR_VOTREE = "api/v0.1/votrees/" + votreeId;
    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(REQUEST_URL_FOR_VOTREE)
        .scheme("http")
        .host(restServer.getHostName())
        .port(restServer.getHostPort())
        .queryParam("votreePassword", votreePassword)
        .queryParam("userId", loginUserId);     
    Votree votree = restTemplate.getForObject(uriBuilder.build().toUri(), Votree.class);
    int proposerId;
    String votreePlainPassword;
    
    try{
      proposerId = votree.getProposerId(); 
      votreePlainPassword = votree.getPlainPassword();
      if(votreePlainPassword != null && !votreePlainPassword.equals(votreePassword)){
        model.addAttribute("message","비밀번호를 확인해주세요.");
        return "error";
      }
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

    /*
     * user Id 와 votree Id를 이용해서 vote_box 가 존재하는지 찾아옵니다.
     */
    final String REQUEST_URL_FOR_VOTE = "api/v0.1/votrees/"+ votreeId +"/topic";
    uriBuilder = UriComponentsBuilder.fromPath(REQUEST_URL_FOR_VOTE)
        .scheme("http")
        .host(restServer.getHostName())
        .port(restServer.getHostPort());
    List<Vote> votes = restTemplate.getForObject(uriBuilder.build().toUri(), ArrayList.class);
    
    model.addAttribute("votree", votree);
    model.addAttribute("proposer", proposer);
    model.addAttribute("voteList",votes);
    return "detail";
  }
  
}
