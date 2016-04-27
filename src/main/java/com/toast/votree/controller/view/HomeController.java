package com.toast.votree.controller.view;

import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.toast.votree.config.RestServer;
import com.toast.votree.service.SessionRedisService;
import com.toast.votree.util.CookieUtil;
import com.toast.votree.vo.Votree;

@Controller
public class HomeController {
  private static Logger logger = LoggerFactory.getLogger(HomeController.class);
  @Autowired
  RestServer restServer;
  @Autowired
  RestTemplate restTemplate;
  @Autowired
  SessionRedisService redisService;
  
  @SuppressWarnings("unchecked")
  @RequestMapping(value = "/", method = RequestMethod.GET)
  public String showMainPage(HttpServletRequest request, Model model 
      ,@RequestParam(value="runningPageNum", defaultValue="0") int runningPageNum
      ,@RequestParam(value="expiredPageNum", defaultValue="0") int expiredPageNum
      ,@RequestParam(value="voteListType", defaultValue="0") int voteListType) {

    final String REQUEST_URL = "api/v0.1/votrees";
    final int CONTENTS_PER_PAGE = 12; // how many contents per page
    final String CALL_CLASSIFICATION = "mainCall";
    
    UriComponentsBuilder uriBuilder = UriComponentsBuilder
        .fromPath(REQUEST_URL)
        .scheme("http")
        .host(restServer.getHostName())
        .port(restServer.getHostPort())
        .queryParam("contentsPerPage",CONTENTS_PER_PAGE)
        .queryParam("callClassification", CALL_CLASSIFICATION)
        .queryParam("voteListType", voteListType)
        .queryParam("runningPageNum", runningPageNum)
        .queryParam("expiredPageNum", expiredPageNum);
    Map<String, List<Votree>> votreeMap = restTemplate.getForObject(uriBuilder.build().toUri(),Map.class);
    model.addAttribute("runningVotrees", votreeMap.get("runningVotrees"));
    model.addAttribute("expiredVotrees", votreeMap.get("expiredVotrees"));
    model.addAttribute("runningPageNum", runningPageNum); // running votree page number
    model.addAttribute("expiredPageNum", expiredPageNum);
    model.addAttribute("contentsPerPage", CONTENTS_PER_PAGE);
    model.addAttribute("voteListType", voteListType);
    
    return "home";
  }

  @RequestMapping(value = "/logout", method = RequestMethod.GET)
  public String logout(HttpServletRequest request, HttpServletResponse response) {
    Cookie cookie = CookieUtil.getCookieByName(request.getCookies(), "VTSESSION");
    redisService.delete(cookie.getValue());
    cookie.setMaxAge(0);
    cookie.setValue(null);
    response.addCookie(cookie);
    return "redirect:/";
  }
}
