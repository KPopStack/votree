package com.toast.votree.vo;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Vote {
  private int id;
  private String votreeId;
  private int weight;
  private String topic;
  private String duplicateYN;
  private String previewYN;
  private int voteItemNum;

  @Valid
  @NotNull @Size(min=2, max=10)
  private List<VoteItem> voteItems;
  private List<Integer> userChoice;
  
  public int getId() {
    return id;
  }
  public Vote setId(int idx) {
    this.id = idx;
    return this;
  }
  
  public String getVotreeId() {
    return votreeId;
  }
  public Vote setVotreeId(String votreeId) {
    this.votreeId = votreeId;
    return this;
  }
  
  public int getWeight() {
    return weight;
  }
  public Vote setWeight(int weight) {
    this.weight = weight;
    return this;
  }
  
  public String getTopic() {
    return topic;
  }
  public Vote setTopic(String topic) {
    this.topic = topic;
    return this;
  }
  
  public String getDuplicateYN() {
    return duplicateYN;
  }
  public Vote setDuplicateYN(String duplicateYN) {
    this.duplicateYN = duplicateYN;
    return this;
  }
  
  public String getPreviewYN() {
    return previewYN;
  }
  public Vote setPreviewYN(String previewYN) {
    this.previewYN = previewYN;
    return this;
  }
  public int getVoteItemNum() {
    return voteItemNum;
  }
  public void setVoteItemNum(int voteItemNum) {
    this.voteItemNum = voteItemNum;
  }

  public List<VoteItem> getVoteItems() {
    return voteItems;
  }
  public void setVoteItems(List<VoteItem> voteItems) {
    this.voteItems = voteItems;
  }
  public void setVoteItemList(List<VoteItem> voteItemList) {
    this.voteItems = voteItemList;
  }
  
  public List<Integer> getUserChoice() {
    return userChoice;
  }
  public void setUserChoice(List<Integer> userChoice) {
    this.userChoice = userChoice;
  }
}
