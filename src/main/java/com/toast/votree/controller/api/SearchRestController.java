package com.toast.votree.controller.api;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.toast.votree.service.UserService;
import com.toast.votree.service.VotreeService;
import com.toast.votree.vo.User;
import com.toast.votree.vo.Votree;

@RestController
public class SearchRestController {
  @Resource(name="userService")
  UserService userService;
  
  @Resource(name="votreeService")
  VotreeService votreeService;
  
  @RequestMapping(value="/api/v0.1/users/search", method=RequestMethod.GET)
  public List<User> getSearchedUser(@RequestParam(value="query") String query) throws UnsupportedEncodingException {
    return userService.findUsersByNameOrEmail(URLDecoder.decode(query,"UTF-8" ));
  }
  
  @RequestMapping(value="/api/v0.1/votrees/search", method=RequestMethod.GET)
  public List<Votree> searchVotreeList(@RequestParam(value="votreeName") String votreeName,
      @RequestParam(value="offset", defaultValue="0") Integer offset) throws UnsupportedEncodingException {
    return votreeService.searchVotrees(URLDecoder.decode(votreeName,"UTF-8" ), offset);
  }

}