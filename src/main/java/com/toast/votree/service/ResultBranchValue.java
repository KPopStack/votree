package com.toast.votree.service;

import java.util.ArrayList;
import java.util.List;

public class ResultBranchValue implements Comparable<ResultBranchValue> {

  private List<Integer> users = new ArrayList<>();
  public ResultBranchValue() {
  }

  public List<Integer> getUsers() {
    return users;
  }

  public ResultBranchValue addUserId(int userId) {
    users.add(userId);
    return this;
  }
  
  public int size() {
    return users.size();
  }

  @Override
  public String toString() {
    return "[users("+ size() +")=" + users + "]";
  }

  @Override
  public int compareTo(ResultBranchValue other) {
    return compare(this, other);
  }
  
  public static int compare(ResultBranchValue o1, ResultBranchValue o2) {
    int x = o1.size(), y = o2.size();
    return (x > y) ? -1 : ((x == y) ? 0 : 1);
  }
}
