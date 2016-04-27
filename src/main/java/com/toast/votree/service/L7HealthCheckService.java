package com.toast.votree.service;

import org.springframework.stereotype.Service;

@Service
public interface L7HealthCheckService {
  public boolean isOnDeploy();
}
