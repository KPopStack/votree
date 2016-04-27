package com.toast.votree.vo;

public class VoteBox {
  int voteBoxId;
  int userId;
  int voteId;
  int voteItemId;
  String votreeId;
  public int getVoteBoxId() {
    return voteBoxId;
  }
  public VoteBox setVoteBoxId(int voteBoxId) {
    this.voteBoxId = voteBoxId;
    return this;
  }
  public int getUserId() {
    return userId;
  }
  public VoteBox setUserId(int userId) {
    this.userId = userId;
    return this;
  }
  public int getVoteId() {
    return voteId;
  }
  public VoteBox setVoteId(int voteId) {
    this.voteId = voteId;
    return this;
  }
  public int getVoteItemId() {
    return voteItemId;
  }
  public VoteBox setVoteItemId(int voteItemId) {
    this.voteItemId = voteItemId;
    return this;
  }
  public String getVotreeId() {
    return votreeId;
  }
  public VoteBox setVotreeId(String votreeId) {
    this.votreeId = votreeId;
    return this;
  }
}
