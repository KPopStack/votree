package com.toast.votree.oauthinfo;

public class Payco {
  private String paycoUri;
  private String paycoUserUri;
  private String paycoClientId;
  private String paycoClientSecret;
  private String paycoServiceProviderCode;
  private String redirectUri; 

  public String getPaycoUserUri() {
    return paycoUserUri;
  }
  public void setPaycoUserUri(String paycoUserUri) {
    this.paycoUserUri = paycoUserUri;
  }  
  public String getPaycoUri() {
    return paycoUri;
  }
  public void setPaycoUri(String paycoUri) {
    this.paycoUri = paycoUri;
  }
  public String getPaycoClientId() {
    return paycoClientId;
  }
  public void setPaycoClientId(String PaycoClientId) {
    this.paycoClientId = PaycoClientId;
  }
  public String getPaycoClientSecret() {
    return paycoClientSecret;
  }
  public void setPaycoClientSecret(String PaycoClientSecret) {
    this.paycoClientSecret = PaycoClientSecret;
  }
  public String getPaycoServiceProviderCode() {
    return paycoServiceProviderCode;
  }
  public void setPaycoServiceProviderCode(String PaycoServiceProviderCode) {
    this.paycoServiceProviderCode = PaycoServiceProviderCode;
  }
  public String getRedirectUri() {
    return redirectUri;
  }
  public void setRedirectUri(String RedirectUri) {
    this.redirectUri = RedirectUri;
  }


  public String getPaycoAuthorizeUri(String state) {
    String uri = getPaycoUri()
        + "authorize?"
        + "client_id=" + getPaycoClientId()
        + "&serviceProviderCode=" + getPaycoServiceProviderCode()
        + "&state=" + state
        + "&redirect_uri=" + getRedirectUri()
        + "&response_type=code";
    return uri;
  }
  public String getPaycoAccessTokenUri(String code,String state) {
    String uri = getPaycoUri()
        + "token?"
        + "grant_type=authorization_code"
        + "&client_id=" + getPaycoClientId()
        + "&client_secret=" + getPaycoClientSecret()
        + "&code=" + code
        + "&state=" + state
        + "&logoutClientIdList=" + getPaycoClientId();
    return uri;
  }
}
