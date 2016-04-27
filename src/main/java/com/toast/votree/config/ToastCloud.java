package com.toast.votree.config;

import java.time.ZonedDateTime;

public class ToastCloud {
  private String account;
  private String tenantName;
  private String userName;
  private String plainPassword;
  private String requestUrlForToken;
  private String requestUrlToUploadObject;
  private String requestUrlToGetObject;
  private String pathForImages;
  private ZonedDateTime expireTime;
  private String accessToken;
  
  public String getAccessToken() {
    return accessToken;
  }
  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }
  public ZonedDateTime getExpireTime() {
    return expireTime;
  }
  public void setExpireTime(ZonedDateTime expireTime) {
    this.expireTime = expireTime;
  }
  public String getAccount() {
    return account;
  }
  public void setAccount(String account) {
    this.account = account;
  }
  public String getTenantName() {
    return tenantName;
  }
  public String getRequestUrlForToken() {
    return requestUrlForToken;
  }
  public void setRequestUrlForToken(String requestUrlForToken) {
    this.requestUrlForToken = requestUrlForToken;
  }
  public String getRequestUrlToUploadObject() {
    return requestUrlToUploadObject;
  }
  public void setRequestUrlToUploadObject(String requestUrlToUploadObject) {
    this.requestUrlToUploadObject = requestUrlToUploadObject;
  }
  public String getRequestUrlToGetObject() {
    return requestUrlToGetObject;
  }
  public void setRequestUrlToGetObject(String requestUrlToGetObject) {
    this.requestUrlToGetObject = requestUrlToGetObject;
  }
  public String getPathForImages() {
    return pathForImages;
  }
  public void setPathForImages(String pathForImages) {
    this.pathForImages = pathForImages;
  }
  public void setTenantName(String tenantName) {
    this.tenantName = tenantName;
  }
  public String getUserName() {
    return userName;
  }
  public void setUserName(String userName) {
    this.userName = userName;
  }
  public String getPlainPassword() {
    return plainPassword;
  }
  public void setPlainPassword(String plainPassword) {
    this.plainPassword = plainPassword;
  }
}
