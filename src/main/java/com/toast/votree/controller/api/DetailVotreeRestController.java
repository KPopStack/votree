package com.toast.votree.controller.api;


import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.toast.votree.service.VotreeService;
import com.toast.votree.util.RestResponse;
import com.toast.votree.vo.Vote;
import com.toast.votree.vo.Votree;

@RestController
public class DetailVotreeRestController {
  @Resource(name="votreeService")
  VotreeService votreeService;

  @RequestMapping(value = "/api/v0.1/votrees/{votreeId}", method = RequestMethod.GET)
  public Votree showDetailVotree(@PathVariable(value = "votreeId") String votreeId
      ,@RequestParam(value = "userId") int userId, @RequestParam(value = "fromResult", defaultValue = "") String from) {
    return votreeService.showDetailVotree(votreeId, userId, from);
  }

  @RequestMapping(value = "/api/v0.1/votrees/{votreeId}", method = RequestMethod.PUT)
  public RestResponse editVotree(@PathVariable(value = "votreeId") String votreeId, @RequestHeader int userId, @RequestBody String body) {
    return votreeService.modifyVotree(votreeId, userId, body);
  }

  @RequestMapping(value = "/api/v0.1/votrees/{votreeId}/topic", method=RequestMethod.GET)
  public List<Vote> showVoteTopic(@PathVariable(value = "votreeId") String votreeId) {
    List <Vote> voteList = votreeService.findVotesByVotreeId(votreeId);
    return voteList;
  }
}
