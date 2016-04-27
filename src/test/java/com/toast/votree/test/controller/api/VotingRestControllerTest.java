package com.toast.votree.test.controller.api;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.toast.votree.controller.api.VotingRestController;
import com.toast.votree.service.VotreeService;
import com.toast.votree.util.JsonUtil;
import com.toast.votree.util.RestResponse;
import com.toast.votree.util.RestResponse.Builder;
import com.toast.votree.vo.Voting;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
    "classpath:spring/applicationContext-test.xml",
    "classpath:spring/servlet-context.xml"
})
@WebAppConfiguration
@EnableWebMvc
public class VotingRestControllerTest {
  private MockMvc mockMvc;
  
  @Mock
  VotreeService votreeService;
  
  VotingRestController controller;
  
  RestResponse restResponse = new Builder().build();
  
  @Autowired
  WebApplicationContext context;
  
  @Autowired
  Validator localValidator;
  
  String votreeId;
  String body;
  int userId;
  Voting voting;

  Map<String, Object> Votree;
  @SuppressWarnings("unchecked")
  @Before
  public void setUp() throws JsonParseException, JsonMappingException, IOException {
    MockitoAnnotations.initMocks(this);
//    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    controller = context.getBean(VotingRestController.class);
    controller.setVotreeService(votreeService);
    
    body = "{\"votreeTitle\":\"123\",\"startDatetime\":\"2016-03-02 00:00\",\"dueDatetime\":\"2016-03-03 00:00\",\"isPrivate\":false,\"turnOut\":0,\"voteList\":[{\"voteName\":\"123\",\"itemList\":[{\"category\":\"텍스트\",\"value\":\"123\"},{\"category\":\"텍스트\",\"value\":\"123\"},{\"category\":\"텍스트\",\"value\":\"123\"}],\"isDuplicate\":false}]}";
    userId = 1543970985;
    votreeId = "1456711217082_2b7add120e6649569747f83f2055b97a";
    
  JsonBuilderFactory factory = Json.createBuilderFactory(null);
  JsonObject value = factory.createObjectBuilder()
      .add("startDatetime", "2016-02-17 00:00:00.0")
      .add("hit", 22)
      .add("dueDatetime", "2016-02-19 00:00:00.0")
      .add("turnout",1)
      .add("subVoteList", Json.createObjectBuilder()
          .add("duplicateYN", "N")
          .add("previewYN", "N")
          .add("voteItemList", Json.createObjectBuilder()
              .add("itemId", 244)
              .add("isVoted", 0)
              .add("value", "d831eacb44fa4cf0be3393f6ca204d88.png")
              .add("categoryId", 2))
          .add("topic","이미지테스트성원지우지마")
          .add("weight", 0)
          .add("voteId", 164))
      .add("proposerId", 8)
      .add("id", 103)
      .add("title","이미지테스트성원지우지마")
      .add("type",1).build();
  Votree = new ObjectMapper().readValue(value.toString(), HashMap.class);
  
  voting = new Voting();
  voting.setVotreeId("votree id");
  voting.setUserId(999);
  voting.setVotes(
      Arrays.asList(new Voting.VoteInVoting().setVoteId(1).setUserChoices(null),
//          new Voting.VoteInVoting().setVoteId(2).setUserChoices(Arrays.asList(11)),
          new Voting.VoteInVoting().setVoteId(3).setUserChoices(null)
          )
      );
  
  }
  @SuppressWarnings("unchecked")
  @Test
  public void getVotreeWithUserChoice_성공() {
    when(votreeService.getVotreeWithUserChoice(votreeId, userId)).thenReturn(Votree);
    Votree = (Map<String, Object>) controller.getVotreeWithUserChoice(votreeId,userId);
    assertNotNull(Votree);
    assertThat(Votree.get("id"),is(103));
  }
  @SuppressWarnings("unchecked")
  @Test
  public void getVotreeWithUserChoice_Test_NULL() {
    votreeId = null;
    when(votreeService.getVotreeWithUserChoice(votreeId, userId)).thenReturn(Votree);
    Votree = (Map<String, Object>) controller.getVotreeWithUserChoice(votreeId,userId);
    assertNotSame(Votree.get("id"), null);
  }
  
  @Test
  public void insertUserChoice_Test_성공() {
    when(votreeService.addUserChoice(anyObject())).thenReturn(restResponse);
    
    try {
      mockMvc.perform(post("/api/v0.1/votrees/{votreeId}/vote", "1457166231913_fc70a5b642ad4a938c7d81e5239537b1")
          .content(JsonUtil.toJsonString(voting))
          .contentType(MediaType.APPLICATION_JSON_UTF8))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
      .andExpect(jsonPath("$").isMap())
      .andExpect(jsonPath("$.header.isSuccessful").value(is(true)));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
//  
  @Test()
  public void insertUserChoice_validation_체크() {
//    Mockito.doNothing().when(votreeService).addUserChoice(anyObject());
//    DbgUtil.logger().debug(DbgUtil.dump(votreeService));
    when(votreeService.addUserChoice(any(Voting.class))).thenReturn(restResponse);
//    String s = "{\"votreeId\":\"1457166231913_fc70a5b642ad4a938c7d81e5239537b1\",\"votes\":[{\"voteId\":\"107\",\"userChoices\":[\"140\"]},{\"voteId\":\"108\",\"userChoice\":[\"144\"]}]}";
//    controller.setVotingValidator(votingValidator);
//    controller.setLocalValidator(localValidator);

    Voting invalidVoting = new Voting();
    invalidVoting.setVotreeId("");
    invalidVoting.setVotes(
        Arrays.asList(new Voting.VoteInVoting().setUserChoices(null),
//            new Voting.VoteInVoting().setVoteId(2).setUserChoices(Arrays.asList(11)),
            new Voting.VoteInVoting().setVoteId(3).setUserChoices(null),
            new Voting.VoteInVoting(),
            new Voting.VoteInVoting(),
            new Voting.VoteInVoting(),
            new Voting.VoteInVoting(),
            new Voting.VoteInVoting(),
            new Voting.VoteInVoting(),
            new Voting.VoteInVoting(),
            new Voting.VoteInVoting(),
            new Voting.VoteInVoting()
            )
        );
   
    try {
      mockMvc.perform(post("/api/v0.1/votrees/{votreeId}/vote", "1457166231913_fc70a5b642ad4a938c7d81e5239537b1")
          .content(JsonUtil.toJsonPretty(invalidVoting))
          .contentType(MediaType.APPLICATION_JSON_UTF8))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
      .andExpect(jsonPath("$").isMap())
      .andExpect(jsonPath("$.header.isSuccessful").value(is(false)));
    } catch (Exception e) {
      e.printStackTrace();
    }

    
//    votingRestController.insertUserChoice("sss", "", "asdf");
  }
  
  @Test()
  public void insertUserChoice_Test_NullPointerException() {
    when(votreeService.addUserChoice(any(Voting.class))).thenReturn(new RestResponse.Builder(false, 400, "NullPointerException").build());
    
    try {
      mockMvc.perform(post("/api/v0.1/votrees/{votreeId}/vote", "1457166231913_fc70a5b642ad4a938c7d81e5239537b1")
          .content(JsonUtil.toJsonString(voting))
          .contentType(MediaType.APPLICATION_JSON_UTF8))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
      .andExpect(jsonPath("$").isMap())
      .andExpect(jsonPath("$.header.resultMessage").value(is("NullPointerException")))
      .andExpect(jsonPath("$.header.isSuccessful").value(is(false)));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  @Test
  public void insertUserChoice_Test_RuntimeException() {
    when(votreeService.addUserChoice(any(Voting.class))).thenReturn(new RestResponse.Builder(false, 400, "이미 투표하였습니다.").build());
    
    try {
      mockMvc.perform(post("/api/v0.1/votrees/{votreeId}/vote", "1457166231913_fc70a5b642ad4a938c7d81e5239537b1")
          .content(JsonUtil.toJsonString(voting))
          .contentType(MediaType.APPLICATION_JSON_UTF8))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
      .andExpect(jsonPath("$").isMap())
      .andExpect(jsonPath("$.header.resultMessage").value(is("이미 투표하였습니다.")))
      .andExpect(jsonPath("$.header.isSuccessful").value(is(false)));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  @Test
  public void adjustUserChoice_Test_성공() {
    when(votreeService.updateUserChoice(voting)).thenReturn(restResponse);
    
    try {
      mockMvc.perform(post("/api/v0.1/votrees/{votreeId}/revote", "1457166231913_fc70a5b642ad4a938c7d81e5239537b1")
          .content(JsonUtil.toJsonString(voting))
          .contentType(MediaType.APPLICATION_JSON_UTF8))
      .andDo(print())
      .andExpect(status().isOk());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
