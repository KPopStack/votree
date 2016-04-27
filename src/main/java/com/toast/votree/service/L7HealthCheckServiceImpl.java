package com.toast.votree.service;

import java.io.File;

import com.toast.votree.util.DbgUtil;

public class L7HealthCheckServiceImpl implements L7HealthCheckService {
  
  private String filePathOnDeployCheck;
  
  public void setFilePathOnDeployCheck(String filePathOnDeployCheck) {
    this.filePathOnDeployCheck = filePathOnDeployCheck;
  }

  @Override
  public boolean isOnDeploy() {
    DbgUtil.logger().debug("file:" + filePathOnDeployCheck);
    File file = new File(filePathOnDeployCheck);
    return file.exists();
  }
}
