package com.toast.votree.vo;

public class ResponseCheck {
  private String uri;
  private long average;
  private long standardDeviation;
  private String date;
public String getUri() {
    return uri;
}
public ResponseCheck setUri(String uri) {
    this.uri = uri;
    return this;
}
public long getAverage() {
    return average;
}
public ResponseCheck setAverage(long average2) {
    this.average = average2;
    return this;
}
public long getStandardDeviation() {
    return standardDeviation;
}
public ResponseCheck setStandardDeviation(long sd) {
    this.standardDeviation = sd;
    return this;
}
public String getDate() {
    return date;
}
public ResponseCheck setDate(String date) {
    this.date = date;
    return this;
}
  
}
