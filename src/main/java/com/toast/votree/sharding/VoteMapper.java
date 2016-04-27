package com.toast.votree.sharding;

import java.util.List;

import com.toast.votree.vo.Vote;
import com.toast.votree.vo.VotreeTopResult;
import com.toast.votree.vo.VotreeTotalResult;

public interface VoteMapper {
  
  public int insertVote(Vote vote);
  
  public void updateVote(Vote vote);
  
  public List<Vote> selectVotesByVotreeID(String votreeId);

  public Vote selectVoteByVoteId(int voteId);
  
  public List<VotreeTopResult> selectTopResultsByVotreeId(String votreeId);
  
  public List<VotreeTotalResult> selectTotalResultsByVotreeId(String votreeId);
}
