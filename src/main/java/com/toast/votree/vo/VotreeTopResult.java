package com.toast.votree.vo;

public class VotreeTopResult {
  private String topic;
  private String value;
  private int voteId;
  private int userChoiceVoteItemId;
  private int topCount;
  private int voteItemCategory;
  
  public VotreeTopResult setTopic(String topic){
    this.topic = topic;
    return this;
  }
  public String getTopic(){
    return topic;
  }
  public VotreeTopResult setValue(String value){
    this.value = value;
    return this;
  }
  public String getValue(){
    return value;
  }
  public VotreeTopResult setVoteId(int voteId){
    this.voteId = voteId;
    return this;
  }
  public int getVoteId(){
    return voteId;
  }
  public VotreeTopResult setUserSelection(int userSelection){
    this.userChoiceVoteItemId = userSelection;
    return this;
  }
  public int getUserSelection(){
    return userChoiceVoteItemId;
  }public VotreeTopResult setTopCount(int topCount){
    this.topCount = topCount;
    return this;
  }
  public int getTopCount(){
    return topCount;
  }
  public VotreeTopResult setVoteItemCategory(int voteItemCategory) {
    this.voteItemCategory = voteItemCategory;
    return this;
  }
  public int getVoteItemCategory(){
    return voteItemCategory;
  }
}
