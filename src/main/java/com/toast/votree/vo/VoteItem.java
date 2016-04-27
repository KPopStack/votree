package com.toast.votree.vo;

import javax.validation.constraints.Size;

public class VoteItem {
  
  private int id;
  @Size(max= 400000)
  private String value;
  private int voteId;
  private int categoryId;
  private int isVoted;
  
  public int getId() {
    return id;
  }
  public VoteItem setId(int id) {
    this.id = id;
    return this;
  }
  public String getValue() {
    return value;
  }
  public VoteItem setValue(String value) {
    this.value = value;
    return this;
  }
  public int getVoteId() {
    return voteId;
  }
  public VoteItem setVoteId(int voteId) {
    this.voteId = voteId;
    return this;
  }
  public int getCategoryId() {
    return categoryId;
  }
  public VoteItem setCategoryId(int categoryId) {
    this.categoryId = categoryId;
    return this;
  }
  public int getIsVoted() {
    return isVoted;
  }
  public VoteItem setIsVoted(int isVoted) {
    this.isVoted = isVoted;
    return this;
  }
}
