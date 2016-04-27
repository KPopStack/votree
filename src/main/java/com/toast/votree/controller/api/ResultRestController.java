package com.toast.votree.controller.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.toast.votree.service.UserService;
import com.toast.votree.service.VotreeService;
import com.toast.votree.util.DbgUtil;
import com.toast.votree.vo.User;
import com.toast.votree.vo.VoteBox;
import com.toast.votree.vo.VotreeTopResult;
import com.toast.votree.vo.VotreeTotalResult;

@RestController
public class ResultRestController {

  @Resource(name="votreeService")
  VotreeService votreeService;

  @Resource(name="userService")
  UserService userService;
  
  @RequestMapping(value="/api/v0.1/votrees/{votree_id}/result",  method = RequestMethod.GET)
  public Map<String,Object> getVotreeResult(@PathVariable(value = "votree_id") String votreeId,
      @RequestParam(value= "userId", defaultValue="") int loginUserId) {

    /* TOP CHOICE 를 가져오기 */
    List<VotreeTopResult> topResults;
    topResults = votreeService.findTopResultsByVotreeId(votreeId);
    boolean textItemOnly = true;
    for(VotreeTopResult votree : topResults) {
      if(votree.getVoteItemCategory() != 1) {
        textItemOnly = false;
      }
    }
    
    /* PERSONAL CHOICE 를 가져오기 */
    List<Integer> sameChoiceUserIds;
    sameChoiceUserIds = votreeService.findVoteBoxesByVotreeId(votreeId, loginUserId);
    
    /* 같은 선택을 한 유저들의 정보 가져오기 */
    List<User> sameChoiceUsers = new ArrayList<>();
    for(int sameChoiceUserId : sameChoiceUserIds) {
      sameChoiceUsers.add(userService.findUserById(sameChoiceUserId)); 
    }
    
    /* 하단에 표시할 전체 결과 가져오기 */
    List<VotreeTotalResult> totalResults;
    totalResults = votreeService.findTotalResultsByVotreeId(votreeId);
    
    Map<String, Object> resultMap = new HashMap<String, Object>();
    resultMap.put("topResults", topResults);
    resultMap.put("sameChoiceUsers", sameChoiceUsers);
    resultMap.put("totalResults", totalResults);
    resultMap.put("textItemOnly", textItemOnly);
    return resultMap;
  }
  
  @RequestMapping(value="/api/v0.1/votes/{vote_id}/result",  method = RequestMethod.GET)
  public Map<String,Object> getVoteResultForChart(@PathVariable(value = "vote_id") int voteId, @RequestParam(value = "votree_id") String votreeId) {
    Map<String, Object> resultMap = votreeService.findVoteResultForChartByVoteId(votreeId, voteId);
    return resultMap;
  }

}
