package com.toast.votree.util;

public class RestResponseHeader {
  
  private boolean isSuccessful;
  private int resultCode;
  private String resultMessage;
  
  public RestResponseHeader(boolean isSuccessful, int resultCode, String resultMessage) {
    this.isSuccessful = isSuccessful;
    this.resultCode = resultCode;
    this.resultMessage = resultMessage;
  }  
  public boolean getIsSuccessful() {
    return isSuccessful;
  }
  public RestResponseHeader setIsSuccessful(boolean isSuccessful) {
    this.isSuccessful = isSuccessful;
    return this;
  }
  public int getResultCode() {
    return resultCode;
  }
  public RestResponseHeader setResultCode(int resultCode) {
    this.resultCode = resultCode;
    return this;
  }
  public String getResultMessage() {
    return resultMessage;
  }
  public RestResponseHeader setResultMessage(String resultMessage) {
    this.resultMessage = resultMessage;
    return this;
  }

}
