package com.toast.votree.vo;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Voting {
  private String votreeId;
  @NotNull
  private Integer userId;

  @NotNull @Size(min=1, max=11)
  @Valid
  List<VoteInVoting> votes;

  public Voting() {
    super();
  }

  public Integer getUserId() {
    return userId;
  }
  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public String getVotreeId() {
    return votreeId;
  }
  public void setVotreeId(String votreeId) {
    this.votreeId = votreeId;
  }

  public List<VoteInVoting> getVotes() {
    return votes;
  }
  public void setVotes(List<VoteInVoting> votes) {
    this.votes = votes;
  }

  public static class VoteInVoting {
    @NotNull
    private Integer voteId;
    private String topic;
    private List<Integer> userChoices;

    public VoteInVoting() {
      super();
    }
    
    public String getTopic() {
      return topic;
    }

    public void setTopic(String topic) {
      this.topic = topic;
    }

    public Integer getVoteId() {
      return voteId;
    }
    public VoteInVoting setVoteId(Integer voteId) {
      this.voteId = voteId;
      return this;
    }

    public List<Integer> getUserChoices() {
      return userChoices;
    }
    public VoteInVoting setUserChoices(List<Integer> userChoices) {
      this.userChoices = userChoices;
      return this;
    }
  }

}


