package com.toast.votree.controller.api;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.toast.votree.service.VotreeService;
import com.toast.votree.vo.Votree;

@RestController
public class HomeRestController {
  @Resource(name="votreeService")
  VotreeService votreeService;
  
  @RequestMapping(value = "/api/v0.1/votrees", method = RequestMethod.GET)
  public Map<String, List<Votree>> showVotreeList(@RequestParam(value="runningPageNum", defaultValue="0") int runningPageNum
      ,@RequestParam(value="expiredPageNum" , defaultValue = "0") int expiredPageNum
      ,@RequestParam(value="voteListType", defaultValue = "0") int voteListType
      ,@RequestParam(value="contentsPerPage", defaultValue = "0") int contentsPerPage
      ,@RequestParam(value="callClassification") final String CALL_CLASSIFICATION
      ,@RequestParam(value="userId", defaultValue = "0") int userId) {
    return votreeService.showVotrees(runningPageNum, expiredPageNum, voteListType, contentsPerPage, CALL_CLASSIFICATION, userId);
  }
}
