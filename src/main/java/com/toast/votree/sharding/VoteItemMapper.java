package com.toast.votree.sharding;

import java.util.List;
import java.util.Map;

import com.toast.votree.vo.VoteItem;

public interface VoteItemMapper {
  public int insertVoteItem(String value, int voteId, int category);
  
  public int insertVoteItem(List<VoteItem> voteItems);
  
  public List<VoteItem> selectVoteItemsByVoteId(int voteId);
  
  public Map<String, Object> selectUserChoiceVoteByVoteItemId(int voteItemId);
}
