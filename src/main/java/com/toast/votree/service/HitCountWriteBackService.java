package com.toast.votree.service;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public interface HitCountWriteBackService {
  
  public void hitCountWriteBack(String votreeId);
  
}
