package com.toast.votree.controller.api;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import com.toast.votree.util.DbgUtil;
import com.toast.votree.util.RestResponse;

@ControllerAdvice(annotations = RestController.class)
//@ControllerAdvice(basePackages="com.toast.votree.controller.api")
public class RestExceptionController {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseBody
  @ResponseStatus(value = org.springframework.http.HttpStatus.BAD_REQUEST)
  protected RestResponse handleDMSRESTException(MethodArgumentNotValidException objException, WebRequest request)
  {
//    DbgUtil.logger().error(objException.getBindingResult().getFieldError("votreeId").getCode());
    return new RestResponse.Builder(false, 444, "").build();
  }
}
