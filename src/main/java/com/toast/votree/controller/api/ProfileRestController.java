package com.toast.votree.controller.api;


import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.toast.votree.service.UserService;
import com.toast.votree.util.DbgUtil;
import com.toast.votree.util.RestResponse;

@RestController
public class ProfileRestController {
  @Resource(name="userService")
  UserService userService;

  @RequestMapping(value ="/api/v0.1/users/{userId}", method = RequestMethod.GET)
  public Object showProfile(@PathVariable(value = "userId") int userId) {
    return userService.findUserById(userId);
  }

  @RequestMapping(value ="/api/v0.1/users/{userId}", method = RequestMethod.PUT)
  public RestResponse modifyProfile(@PathVariable(value = "userId") int userId,
      @RequestBody Map<String, String> jsonBody){
    return userService.modifyUserProfile(userId, jsonBody.get("name"), jsonBody.get("email"));
  }
} 
