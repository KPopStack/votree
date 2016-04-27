package com.toast.votree.service;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.toast.votree.util.RestResponse;
import com.toast.votree.vo.User;

@Service
public interface UserService {
  
  public User findOrSaveUserByOauth(String name, String email, String oauthProvider, String providerKey);
  
  public RestResponse modifyUserProfile(int userId, String name, String email);
  
  public User createUser(String name, String email, String oauthProvider, String providerKey) throws UnsupportedEncodingException;
  
  public User updateUser(int userId, String email, String name);
  
  public User findUserByProviderKey(String providerKey);
  
  public User findUserById(int id);
  
  public List<User> findUsersByNameOrEmail(String nameOrEmail);
}
