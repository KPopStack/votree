package com.toast.votree.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ResultBranchKey implements Cloneable {

  //branch 의 각각의 값은 선택한 항목, 즉, VoteItemId값들의 연결로 구성됨.
  private ArrayList<Integer> branch = new ArrayList<>();
  private int depth;
  
  public ResultBranchKey() {
    branch = new ArrayList<>();
  }
  
  public ResultBranchKey(int depth) {
    branch = new ArrayList<>(depth);
    this.depth = depth; 
  }

  public List<Integer> getBranch() {
    return branch;
  }
  
  public ResultBranchKey addToBranch(int voteItemId) {
    branch.add(voteItemId);
//    branch.sort(comparator);
    return this;
  }
  
  @SuppressWarnings("unchecked")
  @Override
  protected Object clone() throws CloneNotSupportedException {
    ResultBranchKey cloneKey = (ResultBranchKey)super.clone();
    cloneKey.branch = (ArrayList<Integer>) branch.clone();
    cloneKey.depth = depth;
    return cloneKey;
  }

  Comparator<Integer> comparator = new Comparator<Integer>() {
    @Override
    public int compare(Integer o1, Integer o2) {
      return (o1 > o2 ? -1 : (o1 == o2 ? 0 : 1));
    }
  };
  
  public boolean contains(int voteItemId) {
    return branch.contains(voteItemId);
  }

  @Override
  public int hashCode() {
    return toString().hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof ResultBranchKey)) {
      return false;
    }
    ResultBranchKey other = (ResultBranchKey) obj;
    if (this.branch.size() == other.getBranch().size()) {
      return branch.containsAll(other.getBranch());
    }
    
    return toString().equals(obj);
  }

  @Override
  public String toString() {
    String str = "";
    for (Integer voteItemId : branch) {
      str += voteItemId + "|";
    }
    
    return str;
  }
}
