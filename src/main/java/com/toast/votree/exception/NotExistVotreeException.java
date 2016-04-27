
package com.toast.votree.exception;

public class NotExistVotreeException extends RuntimeException {
  private static final long serialVersionUID = 8888879024012726292L;
  private int userId;
  public NotExistVotreeException(Throwable cause, int userId) {
    super(cause);
    this.userId = userId;
  }
  
  public int getUserId() {
	  return userId;
  }
}
