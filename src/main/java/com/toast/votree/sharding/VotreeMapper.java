package com.toast.votree.sharding;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.toast.votree.vo.Vote;
import com.toast.votree.vo.VoteItem;
import com.toast.votree.vo.Votree;

public interface VotreeMapper {
  public int insertVotree(Votree votree);
  
  public void updateVotree(Votree votree);

  public List<Votree> selectVotreesOnRunningState(int limit); 

  public List<Votree> selectVotreesOnRunningStateByUserId(int userId);

  public List<Votree> selectVotreesOnExpiredState(int limit);

  public List<Votree> selectVotreesOnExpiredStateByUserId(int userId);

  public List<Votree> selectJoinedVotreesByUserId(int userId);
  
  public Votree selectVotreeByVotreeId(String votreeId);
  
  public List<Votree> selectVotreesByUserId(int userId);
  
  public Votree selectVotreeByVotreeIdAndUserId(Map<String,Object> map);

  public void updateVotreeHit(Votree votree);

  public Map<String, Object> selectVotreeWithUserChoiceByVotreeIdAndUserId(Map<String, Object> map);
}
