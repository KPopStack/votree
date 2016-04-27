package com.toast.votree.exception;

public class NotExistUserException extends RuntimeException {
  private static final long serialVersionUID = -621704471680854032L;
  public NotExistUserException(Throwable cause) {
    super(cause);
  }
}
