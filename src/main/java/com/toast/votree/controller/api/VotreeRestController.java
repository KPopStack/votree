package com.toast.votree.controller.api;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.toast.votree.service.VotreeService;
import com.toast.votree.util.DbgUtil;
import com.toast.votree.util.RestResponse;
import com.toast.votree.vo.Votree;

@RestController
public class VotreeRestController {
  @Resource(name="votreeService")
  VotreeService votreeService;

  @RequestMapping(value = "api/v0.1/votrees", method = RequestMethod.POST)
  public RestResponse registVotree(@Valid @RequestBody Votree votree, @RequestHeader("userId") int userId){
    return votreeService.createVotree(votree, userId);
  }
}
