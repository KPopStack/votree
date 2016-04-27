package com.toast.votree.vo;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.toast.votree.serializer.CustomDateDeserializer;
import com.toast.votree.serializer.CustomDateSerializer;

public class Votree {
  private String 	id;
  private int     proposerId;
  private int     type;
  private int     turnout;
  private int     hit;
  private String 	title;
  private String  plainPassword;
  private Date		startDatetime;
  private Date		dueDatetime;

  private String   proposerName;
  private int      votingCount;
  private String   isPrivate;
  
  @Valid
  @NotNull @Size(min=1, max=4)
  List <Vote> votes;
  
  public String getId() {
    return id;
  }
  public Votree setId(String id) {
    this.id = id;
    return this;
  }

  public int getProposerId() {
    return proposerId;
  }
  public Votree setProposerId(int proposerId) {
    this.proposerId = proposerId;
    return this;
  }

  public int getType() {
    return type;
  }
  public Votree setType(int type) {
    this.type = type;
    return this;
  }

  public int getTurnout() {
    return turnout;
  }
  public Votree setTurnout(int turnout) {
    this.turnout = turnout;
    return this;
  }

  public int getHit() {
    return hit;
  }
  public Votree setHit(int hit) {
    this.hit = hit;
    return this;
  }

  public String getTitle() {
    return title;
  }
  public Votree setTitle(String title) {
    this.title = title;
    return this;
  }
  
  @JsonDeserialize(using = CustomDateDeserializer.class)
  @JsonSerialize(using = CustomDateSerializer.class)
  public Date getStartDatetime() {
    return startDatetime;
  }
  public Votree setStartDatetime(Date startDatetime) {
    this.startDatetime = startDatetime;
    return this;
  }

  @JsonDeserialize(using = CustomDateDeserializer.class)
  @JsonSerialize(using = CustomDateSerializer.class)
  public Date getDueDatetime() {
    return dueDatetime;
  }
  public Votree setDueDatetime(Date dueDatetime) {
    this.dueDatetime = dueDatetime;
    return this;
  }
  public String getProposerName() {
    return proposerName;
  }
  public Votree setProposerName(String proposerName) {
    this.proposerName = proposerName;
    return this;
  }
  public int getVotingCount() {
    return votingCount;
  }
  public Votree setVotingCount(int votingCount) {
    this.votingCount = votingCount;
    return this;
  }
  public String getPlainPassword() {
    return plainPassword;
  }
  public void setPlainPassword(String plainPassword) {
    this.plainPassword = plainPassword;
  }
  public String getIsPrivate() {
    return isPrivate;
  }
  public void setIsPrivate(String isPrivate) {
    this.isPrivate = isPrivate;
  }
  public List<Vote> getVotes() {
    return votes;
  }
  public void setVotes(List<Vote> votes) {
    this.votes = votes;
  }
}
