package com.toast.votree.controller.view;

import org.springframework.beans.TypeMismatchException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.RestClientException;

import com.toast.votree.exception.DbIndexOutOfBoundsException;
import com.toast.votree.exception.JsonIsNullException;
import com.toast.votree.exception.NotExistUserException;
import com.toast.votree.exception.NotExistVotreeException;
import com.toast.votree.exception.VotreeIsCompleteException;
import com.toast.votree.util.DbgUtil;
import com.toast.votree.util.RestResponse;

@ControllerAdvice()
public class ExceptionController {

  @ExceptionHandler(RuntimeException.class)
  public String runTimeException(RuntimeException e) {
    DbgUtil.logger().error("RUNTIME ERROR : ", e);
    return "error";
  }

  @ExceptionHandler(NotExistUserException.class)
  public String notExistUserIdException(NotExistUserException e, Model model) {
    DbgUtil.logger().error("NOT EXIST USER EXCEPTION : " , e);
    model.addAttribute("message", "존재하지 않는 사용자입니다.");
    return "error";
  }

  @ExceptionHandler(NotExistVotreeException.class)
  public String notExistVotreeException(NotExistVotreeException e, Model model) {
    DbgUtil.logger().error(e.getUserId() + "NOT EXIST VOTREE EXCEPTION : " , e);
    model.addAttribute("message", "존재하지 않는 VOTREE 입니다.");
    return "error";
  }

  @ExceptionHandler(TypeMismatchException.class)
  public String typeMismatchException(TypeMismatchException e) {
    DbgUtil.logger().error("TYPE MISHMATCH EXCEPTION: " , e);
    return "error";
  }

  @ExceptionHandler(JsonIsNullException.class)
  public String jsonIsNullException(JsonIsNullException e) {
    DbgUtil.logger().error("JSON IS NULL");
    return "error";
  }

  @ExceptionHandler(DbIndexOutOfBoundsException.class)
  public String dbIndexOutOfBoundsException(DbIndexOutOfBoundsException e) {
    DbgUtil.logger().error("NO MATCHING DATABASE");
    return "error";
  }

  @ExceptionHandler(RestClientException.class)
  public String restClientException(RestClientException e) {
    DbgUtil.logger().error("NO RETURN FROM RESTTEMPLATE" , e);
    return "error";
  }

  @ExceptionHandler(VotreeIsCompleteException.class)
  public String votreeIsCompleteException(VotreeIsCompleteException e, Model model){
    DbgUtil.logger().debug("VOTREE IS COMPLETED");
    model.addAttribute("message", e.getMessage());
    return "error";
  }
  
//  @ExceptionHandler(MethodArgumentNotValidException.class)
//  @ResponseBody
//  @ResponseStatus(value = org.springframework.http.HttpStatus.BAD_REQUEST)
//  protected RestResponse handleDMSRESTException(MethodArgumentNotValidException objException)
//  {
//    DbgUtil.logger().error(objException.getBindingResult().getFieldError("votreeId").getCode());
//    return new RestResponse.Builder(false, 444, "").build();
//  }
}
