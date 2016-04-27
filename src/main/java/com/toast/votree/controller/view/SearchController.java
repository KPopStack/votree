package com.toast.votree.controller.view;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.toast.votree.config.RestServer;
import com.toast.votree.util.DbgUtil;
import com.toast.votree.vo.User;
import com.toast.votree.vo.Votree;

@Controller
public class SearchController {
  @Autowired
  RestServer restServer;
  @Autowired
  RestTemplate restTemplate;
  
  @SuppressWarnings("unchecked")
  @RequestMapping(value="/search", method=RequestMethod.GET)
  public String getSearchResult(@RequestParam("query")String query, HttpServletRequest request, Model model) throws UnsupportedEncodingException{
    final String REQUEST_URL_FOR_USER = "api/v0.1/users/search";
    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(REQUEST_URL_FOR_USER)
        .scheme("http")
        .host(restServer.getHostName())
        .port(restServer.getHostPort())
        .queryParam("query", URLEncoder.encode(query,"UTF-8" ));
    
    List<User> users = restTemplate.getForObject(uriBuilder.build().toUri(), ArrayList.class);
    model.addAttribute("users", users);
    
    final String REQUEST_URL_FOR_VOTREE = "api/v0.1/votrees/search";
    uriBuilder = UriComponentsBuilder.fromPath(REQUEST_URL_FOR_VOTREE)
        .scheme("http")
        .host(restServer.getHostName())
        .port(restServer.getHostPort())
        .queryParam("votreeName", query);
    
    List<Votree> votrees = restTemplate.getForObject(uriBuilder.build().toUri(), ArrayList.class);
    model.addAttribute("votrees",votrees);

    return "search.result";
  }
}
