package com.toast.votree.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.toast.votree.util.RestResponse;
import com.toast.votree.vo.Vote;
import com.toast.votree.vo.VoteBox;
import com.toast.votree.vo.Voting;
import com.toast.votree.vo.Votree;
import com.toast.votree.vo.VotreeTopResult;
import com.toast.votree.vo.VotreeTotalResult;

@Service
public interface VotreeService {
  /*
   * Votree-투표그룹
   */
  public List<Votree> searchVotrees(String votreeName, Integer offset);
 
  //public String getBase64StringByFileName(String fileName);
  public Map<String, Object> getVotreeWithUserChoice(String votreeId, int userId);
  
  public Votree showDetailVotree(String votreeId, int userId, String from);
  
  public RestResponse modifyVotree(String votreeId, int userId, String body);
  
  public Map<String, List<Votree>> showVotrees(int runningPageNum, int expiredPageNum
      , int voteListType, int contentsPerPage, final String CALL_CLASSIFICATION, int userId);
  
  public RestResponse createVotree(Votree votree, int userId);
  
  /*
   * 투표하기
   */
  public RestResponse addUserChoice(Voting voting);
  
  public RestResponse updateUserChoice(Voting body);
  
  /*
   * 단일투표
   */
//  public int addVote(Vote vote);
  
  public List<Vote> findVotesByVotreeId(String votreeId);

//  public Vote findVoteByVoteId(int voteId);
  
  public List<VotreeTopResult> findTopResultsByVotreeId(String votreeId);
  
  public List<VotreeTotalResult> findTotalResultsByVotreeId(String votreeId);
  
  public List<Integer> findVoteBoxesByVotreeId(String votreeId, int userId);
  /*
   * 투표항목
   */
//  public int addVoteItem(String value, int voteId, int category);
  
//  public List<VoteItem> findVoteItemsByVoteId(int voteId);
  
  /*
   * 투표 결과
   */
  public Map<String, Object> findVoteResultForChartByVoteId(String votreeId, int voteId);
  
//  public Map<String, Object> findVoteResultAllInVotree(String votreeId);
  public List<Map<String, Object>> calcVotreeResultForBestChoice(String votreeId);
}
