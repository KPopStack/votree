package com.toast.votree.test.controller.api;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.toast.votree.controller.api.HomeRestController;
import com.toast.votree.service.VotreeService;
import com.toast.votree.util.DbgUtil;
import com.toast.votree.vo.Votree;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@WebAppConfiguration
@ContextConfiguration(locations = {
    "classpath:spring/applicationContext-test.xml",
    "classpath:spring/servlet-context.xml"
})
@RunWith(MockitoJUnitRunner.class)
public class HomeRestControllerTest {

  @Mock
  VotreeService votreeService;
  
  @InjectMocks
  HomeRestController controller;
  
  private MockMvc mockMvc;
  
  List<Votree> runningVotrees = new ArrayList<>();
  List<Votree> expiredVotrees = new ArrayList<>();
  
  @Before
  public void setup() throws ParseException {
      MockitoAnnotations.initMocks(this);
      this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
      
      runningVotrees = Arrays.asList(
          new Votree().setId(UUID.randomUUID().toString()).setTitle("진행1"),
          new Votree().setId(UUID.randomUUID().toString()).setTitle("진행2"),
          new Votree().setId(UUID.randomUUID().toString()).setTitle("진행3")
          );

      expiredVotrees = Arrays.asList(
          new Votree().setId(UUID.randomUUID().toString()).setTitle("완료1"),
          new Votree().setId(UUID.randomUUID().toString()).setTitle("완료2"),
          new Votree().setId(UUID.randomUUID().toString()).setTitle("완료3")
          );
  }
  
  @Test
  public void testHomeRestControllerCallByMain() throws Exception{
    Map<String, List<Votree>> totalVotree = new HashMap<>();
    totalVotree.put("runningVotrees", runningVotrees);
    totalVotree.put("expiredVotrees", expiredVotrees);
    when(votreeService.showVotrees(anyInt(), anyInt(), anyInt(), anyInt(), eq("mainCall"), anyInt())).thenReturn(totalVotree);

    Votree expectedVotree = runningVotrees.get(0);
    DbgUtil.logger().info(expectedVotree.getId() + ", " + expectedVotree.getTitle());
    mockMvc.perform(get("/api/v0.1/votrees")
      .param("runningPageNum", "0")
      .param("expiredPageNum", "0")
      .param("voteListType", "0")
      .param("contentsPerPage", "12")
      .param("callClassification", "mainCall")
      .param("userId", "1")
      ).andDo(print())
		    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
		    .andExpect(status().isOk())
		    .andExpect(jsonPath("$.runningVotrees").isArray())
		    .andExpect(jsonPath("$.runningVotrees[0].id").value(expectedVotree.getId()))
		    .andExpect(jsonPath("$.runningVotrees[4]").doesNotExist());
		
    expectedVotree = expiredVotrees.get(0);
    mockMvc.perform(get("/api/v0.1/votrees")
        .param("runningPageNum", "0")
        .param("expiredPageNum", "0")
        .param("voteListType", "0")
        .param("contentsPerPage", "12")
        .param("callClassification", "mainCall")
        .param("userId", "1")
        ).andDo(print())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.expiredVotrees").isArray())
      .andExpect(jsonPath("$.expiredVotrees[0].id").value(expectedVotree.getId()))
      .andExpect(jsonPath("$.runningVotrees[4]").doesNotExist());
  }
  
  
  @Test
  @Ignore
  public void testHomeRestControllerCallByProfile() throws Exception{
    mockMvc.perform(get("/api/v0.1/votrees")
        .param("runningPageNum", "0")
        .param("expiredPageNum", "0")
        .param("voteListType", "0")
        .param("contentsPerPage", "12")
        .param("callClassification", "profileCall")
        .param("userId", "1")
        ).andDo(print())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.runningVotrees").isArray())
      .andExpect(jsonPath("$.runningVotrees[0]").doesNotExist());
  }
  
}


