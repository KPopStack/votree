package com.toast.votree.util;

public class RestResponse {
  RestResponseHeader header;
  String body;

  private RestResponse(Builder builder) {
    this.header = new RestResponseHeader(builder.isSuccessful, builder.resultCode, builder.resultMessage);
    this.body = builder.body;
  }
  
  public RestResponseHeader getHeader() {
    return header;
  }

  public String getBody() {
    return body;
  }

  public static class Builder {
    private boolean isSuccessful;
    private int resultCode;
    private String resultMessage;

    String body;

    public Builder() {
      super();
      this.isSuccessful = true;
      this.resultCode = 200;
      this.resultMessage = "200 - OK";
    }
    
    public Builder(String resultMessage) {
      super();
      this.isSuccessful = true;
      this.resultCode = 200;
      this.resultMessage = resultMessage;
    }
    
    public Builder(boolean isSuccessful, int resultCode) {
      super();
      this.isSuccessful = isSuccessful;
      this.resultCode = resultCode;
    }
    
    public Builder(boolean isSuccessful, int resultCode, String resultMessage) {
      super();
      this.isSuccessful = isSuccessful;
      this.resultCode = resultCode;
      this.resultMessage = resultMessage;
    }

    public Builder isSuccessful(boolean isSuccessful) {
      this.isSuccessful = isSuccessful;
      return this;
    }

    public Builder resultCode(int resultCode) {
      this.resultCode = resultCode;
      return this;
    }

    public Builder resultMessage(String resultMessage) {
      this.resultMessage = resultMessage;
      return this;
    }

    public Builder body(String body) {
      this.body = body;
      return this;
    }

    public RestResponse build() {
      return new RestResponse(this);
    }
  }

}
