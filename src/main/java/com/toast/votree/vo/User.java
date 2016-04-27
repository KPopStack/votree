package com.toast.votree.vo;

import java.util.Date;

import com.toast.votree.oauthinfo.GravatarUtils;

public class User {
  private int       id;
  private String     email;
  private String     name;
  private String     oauthProvider;
  private String     profileUrl;
  private String     providerKey;
  private Date    updatedAt;
  private Date    createdAt;

  private boolean alreadyVote;
  
  public int getId() {
    return id;
  }
  public User setId(int id) {
    this.id = id;
    return this;
  }

  public String getEmail() {
    return email;
  }
  public User setEmail(String email) {
    this.email = email;
    return this;
  }

  public String getName() {
    return name;
  }
  public User setName(String name) {
    this.name = name;
    return this;
  }
  public String getOauthProvider() {
    return oauthProvider;
  }
  public User setOauthProvider(String oauthProvider) {
    this.oauthProvider = oauthProvider;
    return this;
  }
  public String getProviderKey() {
    return providerKey;
  }
  public User setProviderKey(String providerKey) {
    this.providerKey = providerKey;
    return this;
  }    
  public Date getUpdatedAt() {
    return updatedAt;
  }
  public User setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }
  public Date getCreatedAt() {
    return createdAt;
  }
  public User setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
    return this;
  }
  public User alreadyVote(boolean alreadyVote) {
    this.alreadyVote = alreadyVote;
    return this;
  }
  public boolean alreadyVote() {
    return alreadyVote;
  }
  public String getProfileUrl() {
    return "http://www.gravatar.com/avatar/" + GravatarUtils.getUrl(email) + "?d=mm";
  }
}

