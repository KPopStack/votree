package com.toast.votree.test.controller.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.toast.votree.controller.api.DetailVotreeRestController;
import com.toast.votree.service.VotreeService;
import com.toast.votree.vo.Vote;

import junit.framework.Assert;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
    "classpath:spring/applicationContext-test.xml",
    "classpath:spring/servlet-context.xml"
})
@Transactional
@WebAppConfiguration
public class DetailVotreeRestControllerTest {
  private MockMvc mockMvc;
  
  @Autowired
  WebApplicationContext wac;
  
  @Mock
  VotreeService votreeService;
  
  @InjectMocks
  DetailVotreeRestController restController;
  
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(restController).build();
  }
  
  @Test
  public void testShowDetailVotreeInfo() {
    try {
      this.mockMvc.perform(get("/api/v0.1/votrees/{votreeId}",1)
          .param("userId","16"));
    } catch (Exception e) {
      Assert.fail();
    }
  }
  /* 정상적인 수정 데이터가 들어올 때 (비공개)*/ 
  @Test
  public void testEditVotree_비공개() {
    String inputData = "{"
        + "\"votreeTitle\":\"새로운투표\","
        + "\"startDatetime\":\"2016-01-01 10:00\","
        + "\"dueDatetime\":\"2016-02-15 15:00\","
        + "\"isPrivate\":true,"
        + "\"password\":\"1234\","
        + "\"voteList\":["
        + "{"
        + "\"isDuplicate\":\"Y\""
        + "}"
        + "]"
        + "}";
    try {
      this.mockMvc.perform(put("/api/v0.1/votrees/{votreeId}",1)
          .header("userId",16)
          .content(inputData)
          .contentType(MediaType.APPLICATION_JSON_UTF8))
      .andExpect(status().isOk());
    } catch (Exception e) {
      Assert.fail();
    }
  }
  /* 정상적인 수정 데이터가 들어올 때 (공개)*/ 
  @Test
  public void testEditVotree1_공개() throws Exception {
    String inputData = "{"
        + "\"votreeTitle\":\"새로운투표\","
        + "\"startDatetime\":\"2016-01-01 10:00\","
        + "\"dueDatetime\":\"2016-02-15 15:00\","
        + "\"isPrivate\":false,"
        + "\"voteList\":["
          + "{"
            + "\"isDuplicate\":\"Y\""
          + "}"
        + "]"
        + "}";
    this.mockMvc.perform(put("/api/v0.1/votrees/{votreeId}",1)
        .header("userId",16)
        .content(inputData)
        .contentType(MediaType.APPLICATION_JSON_UTF8))
          .andExpect(status().isOk());
    }
  
  /* 넘어온 json에 누락이 있을 때 */ 
  @Test
  public void testEditVotree1() throws Exception {
    String inputData = "{"
//        + "\"votreeTitle\":\"새로운투표\","
        + "\"startDatetime\":\"2016-01-01 10:00\","
        + "\"dueDatetime\":\"2016-02-15 15:00\","
        + "\"isPrivate\":false,"
        + "\"voteList\":["
          + "{"
            + "\"isDuplicate\":\"Y\""
          + "}"
        + "]"
        + "}";
    this.mockMvc.perform(put("/api/v0.1/votrees/{votreeId}","6")
        .header("userId",16)
        .content(inputData)
        .contentType(MediaType.APPLICATION_JSON_UTF8))
          .andExpect(status().isOk());
    }
  
  @Test
  public void testShowVoteTopic() {
    List<Vote> votes = Arrays.asList(new Vote().setId(1),
        new Vote().setId(2),
        new Vote().setId(3));
    
    when(votreeService.findVotesByVotreeId(anyString())).thenReturn(votes);
    try {
      this.mockMvc.perform(get("/api/v0.1/votrees/{votreeId}/topic","6"))
        .andExpect(status().isOk())
        .andDo(print())
        .andExpect(jsonPath("$").isArray());
    } catch (Exception e) {
      Assert.fail();
    }
  }
}
