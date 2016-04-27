package com.toast.votree.sharding;

import java.util.Map;

import com.toast.votree.vo.Voting;

public interface VotingMapper {
  
  public int incrementTurnOut(String votreeId);
  
  public int insertToVoteBox(Voting voting);

  public int revoteToVoteBox(Map<String, Object> map);

  //아직 사용 중이지 않으나 추후 고려 
  public int deleteByVoteId(Voting voting);
}
