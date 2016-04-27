package com.toast.votree.oauthinfo;

import java.io.IOException;
import java.net.MalformedURLException;
public class Facebook {
  private String facebookAppId;
  private String facebookAppSecret;
  private String redirectUri;

  public String getFacebookAppId() {
    return facebookAppId;
  }
  public void setFacebookAppId(String facebookAppId) {
    this.facebookAppId = facebookAppId;
  }  

  public String getFacebookAppSecret() {
    return facebookAppSecret;
  }
  public void setFacebookAppSecret(String facebookAppSecret) {
    this.facebookAppSecret= facebookAppSecret;
  }

  public String getRedirectUri() {
    return redirectUri;
  }
  public void setRedirectUri(String redirectUri) {
    this.redirectUri = redirectUri;
  }

  public String getFacebookAuthorizeUri(String state) {
    String uri = "http://www.facebook.com/dialog/oauth?"
        + "client_id=" + getFacebookAppId()
        + "&redirect_uri=" + getRedirectUri()
        + "&scope=email"
        + "&state=" + state;
    return uri;
  }
  //TODO REVIEW
  public  String getProcessingAccessToken(String accessToken) throws MalformedURLException, IOException {

    if(accessToken == null || accessToken.length() == 0 ) {
      throw new RuntimeException("ERROR: Didn't get Access Token.");
    }
    accessToken = accessToken.split("=")[1].split("&")[0];//access_token=12345&.. String에서 12345를 꺼냅니다.
    if (accessToken.startsWith("{")) {
      throw new RuntimeException("ERROR: Access Token Invalid: " + accessToken);
    }
    return accessToken;
  }

  public String getFacebookGraphUri(String code) {
    String fbGraphUrl = "https://graph.facebook.com/oauth/access_token?"
        + "client_id=" + getFacebookAppId()
        + "&redirect_uri=" + getRedirectUri()
        + "&client_secret=" + getFacebookAppSecret()
        + "&code=" + code;
    return fbGraphUrl;
  }

  public String getFacebookUserUri(String accessToken) {

    String userInfoUri = "https://graph.facebook.com/v2.5/me?"
        + "fields=id,name,email"
        + "&access_token=" + accessToken;
    return userInfoUri;
  }
}
