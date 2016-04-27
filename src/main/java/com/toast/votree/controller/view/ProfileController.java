package com.toast.votree.controller.view;

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
import com.toast.votree.exception.NotExistUserException;
import com.toast.votree.service.SessionRedisService;
import com.toast.votree.util.CookieUtil;
import com.toast.votree.util.DbgUtil;
import com.toast.votree.vo.User;
import com.toast.votree.vo.Votree;

@Controller
public class ProfileController {
  @Autowired
  RestServer restServer;
  @Autowired
  RestTemplate restTemplate;
  
  @Autowired
  SessionRedisService redisService;
  
  @SuppressWarnings("unchecked")
  @RequestMapping(value = "/profiles/{userId}" , method = RequestMethod.GET )
  public Object showProfilePage(@PathVariable(value = "userId") int userId, Model model,
      @RequestParam(value="modifiedName", defaultValue="") String modifiedName,
      HttpServletRequest request) throws NotExistUserException {

    Cookie cookie = CookieUtil.getCookieByName(request.getCookies(), "VTSESSION");
    final String requestUserPath = "api/v0.1/users/" + userId;
    final String CALL_CLASSIFICATION = "profileCall";
    
    /*
     * Get User
     */
    UriComponentsBuilder uriUserGetBuilder = UriComponentsBuilder
        .fromPath(requestUserPath)
        .scheme("http")
        .host(restServer.getHostName())
        .port(restServer.getHostPort());
    
    User user = restTemplate.getForObject(uriUserGetBuilder.build().toUri(), User.class);
    model.addAttribute("user",user);
    if(cookie != null){
      model.addAttribute("userId", redisService.findDataByKeyInHashKey(cookie.getValue(), "userId"));
      model.addAttribute("userName", redisService.findDataByKeyInHashKey(cookie.getValue(), "userName"));
    }
    
    /*
     * 프로필 Name이 수정된 경우 레디스에도 반영
     */
    
    if(!modifiedName.equals("") && cookie != null){
      cookie = CookieUtil.getCookieByName(request.getCookies(), "VTSESSION");
      redisService.createDataByKeyInHashKey(cookie.getValue(), "userName", modifiedName);
      DbgUtil.logger().debug("USER NAME IN PROFILE VIEW CON : " + modifiedName);
      model.addAttribute("userName", modifiedName);
    }

    
    /*
     * Get Votree
     */
    final String requestVotreePath = "api/v0.1/votrees"; 
    try{
      UriComponentsBuilder uriVotreeGetBuilder = UriComponentsBuilder
          .fromPath(requestVotreePath)
          .scheme("http")
          .host(restServer.getHostName())
          .port(restServer.getHostPort())
          .queryParam("userId", user.getId())
          .queryParam("callClassification",CALL_CLASSIFICATION);
      Map<String, List<Votree>> votree = restTemplate.getForObject(uriVotreeGetBuilder.build().toUri(), Map.class);
      model.addAttribute("runningVotrees", votree.get("runningVotrees"));
      model.addAttribute("expiredVotrees", votree.get("expiredVotrees"));
      model.addAttribute("joinedVotrees", votree.get("joinedVotrees"));
    }catch(NullPointerException e){
      throw new NotExistUserException(e);
    }
    return "profile";
  }
}
