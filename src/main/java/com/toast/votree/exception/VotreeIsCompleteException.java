package com.toast.votree.exception;

/*
 * 완료된 Votree의 voting 페이지에 접할 때 나는 Exception
 */
public class VotreeIsCompleteException extends RuntimeException{
  private static final long serialVersionUID = 2921726693604363923L;
  private String errorMsg;
  public VotreeIsCompleteException(String msg) {
    super(msg);
    this.errorMsg = msg; 
  }
  
  public String getErrorMsg() {
    return errorMsg;
  }
 
  
}
