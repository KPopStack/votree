package com.toast.votree.controller.api;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.toast.votree.service.VotreeService;
import com.toast.votree.util.DbgUtil;
import com.toast.votree.util.RestResponse;
import com.toast.votree.vo.Voting;



@RestController
public class VotingRestController {
  @Resource(name="votreeService")
  VotreeService votreeService;

  public void setVotreeService(VotreeService votreeService) {
    this.votreeService = votreeService;
  }

  @RequestMapping(value = "api/v0.1/votrees/{votreeId}/votes", method = RequestMethod.GET)
  public Object getVotreeWithUserChoice(@PathVariable(value = "votreeId") String votreeId, @RequestHeader("userId") int userId) {
    return votreeService.getVotreeWithUserChoice(votreeId, userId);
  }

  @RequestMapping(value = "api/v0.1/votrees/{votreeId}/vote", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
  public RestResponse insertUserChoice(@PathVariable("votreeId") String votreeId, @Valid @RequestBody Voting voting) {
    voting.setVotreeId(votreeId);
    return votreeService.addUserChoice(voting);
  }

  @RequestMapping(value = "api/v0.1/votrees/{votreeId}/revote", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
  public RestResponse adjustUserChoice(@PathVariable("votreeId") String votreeId, @Valid @RequestBody() Voting voting) {
    voting.setVotreeId(votreeId);
    return votreeService.updateUserChoice(voting);
  }

  @ExceptionHandler( MethodArgumentNotValidException.class)
  @ResponseBody
  @ResponseStatus(value = org.springframework.http.HttpStatus.BAD_REQUEST)
  protected RestResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e)
  {
    String errorMessage = "";
    for (FieldError error : e.getBindingResult().getFieldErrors()) {
      errorMessage += error.getField() +" - "+ error.getDefaultMessage() + "</br>";
      DbgUtil.logger().error(error.getField() +" "+ error.getDefaultMessage() + " In code: " + DbgUtil.dumpShortStyle(error.getCodes()));
    }
    
    return new RestResponse.Builder(false, 444, errorMessage).build();
  }
}
