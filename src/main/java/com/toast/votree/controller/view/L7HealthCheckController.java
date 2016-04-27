package com.toast.votree.controller.view;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.toast.votree.service.L7HealthCheckService;

@Controller
public class L7HealthCheckController {

  @Autowired
  L7HealthCheckService l7HealthCheckService;

  public void setL7HealthCheckService(L7HealthCheckService l7HealthCheckService) {
    this.l7HealthCheckService = l7HealthCheckService;
  }

  @RequestMapping("/monitor/l7check")
  @ResponseBody
  public String getHealthCheck(HttpServletResponse response) {
    if (l7HealthCheckService.isOnDeploy()) {
      response.setStatus(HttpStatus.SC_SERVICE_UNAVAILABLE);
    }
    return "OK";
  }
}
