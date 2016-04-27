package com.toast.votree.test.controller.api;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.toast.votree.controller.api.ResultRestController;
import com.toast.votree.service.UserService;
import com.toast.votree.service.VotreeService;
import com.toast.votree.vo.User;
import com.toast.votree.vo.VotreeTopResult;
import com.toast.votree.vo.VotreeTotalResult;

@RunWith(MockitoJUnitRunner.class)
public class ResultRestControllerTest {

  @Mock
  VotreeService votreeService;
  
  @Mock
  UserService userService;
  
  @InjectMocks
  ResultRestController controller;
  
  private MockMvc mockMvc;
  
  List<VotreeTotalResult> totalResults = new ArrayList<>();
  List<VotreeTopResult> topResults = new ArrayList<>();
  Map<String, Object> resultMap = new HashMap<>();
  List<Integer> sameChoiceUserIds = new ArrayList<>();
  User user = new User();
  @Before
  public void setup() throws ParseException {
      MockitoAnnotations.initMocks(this);
      this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
      
      VotreeTotalResult votreeTotalResult = new VotreeTotalResult();
      votreeTotalResult.setVoteId(1);
      votreeTotalResult.setUserSelection(1);
      votreeTotalResult.setVotePerSelection(100);
      votreeTotalResult.setValue("테스트");
      totalResults.add(votreeTotalResult);
      
      votreeTotalResult = new VotreeTotalResult();
      votreeTotalResult.setVoteId(1);
      votreeTotalResult.setUserSelection(1);
      votreeTotalResult.setVotePerSelection(50);
      votreeTotalResult.setValue("테스트2");
      totalResults.add(votreeTotalResult);
      
      votreeTotalResult = new VotreeTotalResult();
      votreeTotalResult.setVoteId(1);
      votreeTotalResult.setUserSelection(1);
      votreeTotalResult.setVotePerSelection(25);
      votreeTotalResult.setValue("테스트3");
      totalResults.add(votreeTotalResult);
      
      VotreeTopResult votreeTopResult = new VotreeTopResult();
      votreeTopResult.setTopCount(100);
      votreeTopResult.setTopic("TOPIC 4");
      votreeTopResult.setUserSelection(1);
      votreeTopResult.setValue("VAL 4");
      votreeTopResult.setVoteId(1);
      topResults.add(votreeTopResult);
      
      votreeTopResult = new VotreeTopResult();
      votreeTopResult.setTopCount(50);
      votreeTopResult.setTopic("TOPIC 5");
      votreeTopResult.setUserSelection(1);
      votreeTopResult.setValue("VAL 5");
      votreeTopResult.setVoteId(1);
      topResults.add(votreeTopResult);
      
      votreeTopResult = new VotreeTopResult();
      votreeTopResult.setTopCount(25);
      votreeTopResult.setTopic("TOPIC 6");
      votreeTopResult.setUserSelection(1);
      votreeTopResult.setValue("VAL 6");
      votreeTopResult.setVoteId(1);
      topResults.add(votreeTopResult);
      
      sameChoiceUserIds.addAll(Arrays.asList(
          1,2,3,4
          ));
      
      user.setId(1).setEmail("seongwon@gmail.com").setName("전땅우");
      
      Map<String, String> seriesMap = new HashMap<>();
      seriesMap.put("data", "100");
      seriesMap.put("name", "강남");
      
      resultMap.put("voteId", 1);
      resultMap.put("series", seriesMap);
  }
  
  @Test
  public void testGetVotreeResult() throws Exception{
//    topResults = votreeService.findTopResultsByVotreeId(votreeId);
    when(votreeService.findTopResultsByVotreeId(anyString())).thenReturn(topResults);
    when(votreeService.findTotalResultsByVotreeId(anyString())).thenReturn(totalResults);
    when(votreeService.findVoteBoxesByVotreeId(anyString(), anyInt())).thenReturn(sameChoiceUserIds);
    when(userService.findUserById(anyInt())).thenReturn(user);
    mockMvc.perform(get("/api/v0.1/votrees/{votree_id}/result",1).param("userId", "123")
        ).andDo(print())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.totalResults").isArray())
      .andExpect(jsonPath("$.totalResults[0].voteId").value(1))
      .andExpect(jsonPath("$.totalResults[0].userSelection").value(1))
      .andExpect(jsonPath("$.totalResults[0].votePerSelection").value(100))
      .andExpect(jsonPath("$.totalResults[0].value").value("테스트"))
      .andExpect(jsonPath("$.topResults").isArray())
      .andExpect(jsonPath("$.topResults[0].voteId").value(1))
      .andExpect(jsonPath("$.topResults[0].userSelection").value(1))
      .andExpect(jsonPath("$.topResults[0].topCount").value(100))
      .andExpect(jsonPath("$.topResults[0].topic").value("TOPIC 4"))
      .andExpect(jsonPath("$.topResults[0].value").value("VAL 4"));
  }
  
  @Test
  public void testGetVoteResultForChart() throws Exception{
    when(votreeService.findVoteResultForChartByVoteId(anyString(), anyInt())).thenReturn(resultMap);
    
    mockMvc.perform(get("/api/v0.1/votes/{vote_id}/result",1).param("votree_id", "dummyValue")
        ).andDo(print())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isMap())
      .andExpect(jsonPath("$.voteId").value(1))
      .andExpect(jsonPath("$.series.data").value("100"))
      .andExpect(jsonPath("$.series.name").value("강남"));
  }

}
