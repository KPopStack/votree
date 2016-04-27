package com.toast.votree.vo;

public class VotreeTotalResult {
  private int voteId;
  private int userChoiceVoteItemId;
  private int votePerSelectionNum; 
  private String value;
  
  public VotreeTotalResult setVoteId(int voteId){
    this.voteId = voteId;
    return this;
  }
  public int getVoteId(){
    return voteId;
  }
  public VotreeTotalResult setUserSelection(int userChoiceVoteItemId){
    this.userChoiceVoteItemId = userChoiceVoteItemId;
    return this;
  }
  public int getUserSelection(){
    return userChoiceVoteItemId;
  }
  public VotreeTotalResult setVotePerSelection(int votePerSelection){
    this.votePerSelectionNum = votePerSelection;
    return this;
  }
  public int getVotePerSelection(){
    return votePerSelectionNum;
  }
  public VotreeTotalResult setValue(String value){
    this.value = value;
    return this;
  }
  public String getValue(){
    return value;
  }
}
