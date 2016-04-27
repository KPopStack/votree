package com.toast.votree.controller.api;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.toast.votree.service.UserService;
import com.toast.votree.util.DbgUtil;
import com.toast.votree.vo.User;

@RestController
public class OauthRestController {
  @Resource(name="userService")
  UserService userService;
  
  @RequestMapping(value = "/api/v0.1/users", method = RequestMethod.GET)
  public User oauthUserControl(@RequestParam(value = "provider_key") String providerKey
      , @RequestParam(value = "oauth_provider") String oauthProvider
      , @RequestParam(value = "name") String name
      , @RequestParam(value = "email", defaultValue = "") String email
      , @RequestParam(value = "profile_url", defaultValue = "") String profileUrl) throws UnsupportedEncodingException {
    return userService.findOrSaveUserByOauth(URLDecoder.decode(name, "UTF-8"), email, oauthProvider, providerKey);
  }
}
