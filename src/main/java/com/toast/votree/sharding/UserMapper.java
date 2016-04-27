package com.toast.votree.sharding;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.toast.votree.vo.User;

public interface UserMapper {
  public User selectUserById(int userId);
  public int insertUser(User user);
  public void updateUser(User user);
  public List<User> selectUsersByNameOrEmail(String input);
  public User selectUserByProviderKey(String providerKey);
}
