package com.toast.votree.sharding;

import java.util.List;
import java.util.Map;

import com.toast.votree.vo.Voting;

import com.toast.votree.vo.VoteBox;

public interface VoteResultMapper {
  public Map<String, Object> selectVoteResultByVoteId(int voteId);
  
  public Map<String, Object> selectVoteResultsInVotree(String votreeId);
  
  public List<Voting> selectVotingsByVotreeId(String votreeId);
  public List<VoteBox> selectVoteBoxesByVotreeId(String votreeId);
}
