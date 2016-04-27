package com.toast.votree.test.controller.view;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.toast.votree.controller.view.L7HealthCheckController;
import com.toast.votree.service.L7HealthCheckServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class L7HealthCheckTest {
  MockMvc mockMvc;
  
  @InjectMocks
  L7HealthCheckController controller;
  
  L7HealthCheckServiceImpl l7HealthCheckService = new L7HealthCheckServiceImpl();
  
  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    
    l7HealthCheckService.setFilePathOnDeployCheck("d:\\toast\\ondeploy.txt");
    controller.setL7HealthCheckService(l7HealthCheckService);
  }

  @Test
  public void l7HealthCheck_OK() throws Exception {
    mockMvc.perform(get("/monitor/l7check"))
        .andDo(print())
        .andExpect(status().isOk());
  }
  
  @Test
  public void l7HealthCheck_Forced_Fail() throws Exception {
    File file = new File("d:\\toast\\ondeploy.txt");
    file.createNewFile();
    mockMvc.perform(get("/monitor/l7check"))
        .andDo(print())
        .andExpect(status().is5xxServerError());
    
    file.delete();
  }
}
