package com.toast.votree.test.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.toast.votree.manager.DbShardingManager;
import com.toast.votree.service.DatabaseMapperRedisService;
import com.toast.votree.service.VotreeServiceImpl;
import com.toast.votree.sharding.VotingMapper;
import com.toast.votree.util.RestResponse;
import com.toast.votree.vo.Voting;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
    "classpath:spring/applicationContext-test.xml",
    "classpath:spring/servlet-context.xml"
})
public class VotingServiceTest {
  
  @Mock
  DatabaseMapperRedisService databaseMapperRedisService;
  @Mock
  DbShardingManager dbShardingManager;
  
  @InjectMocks
  VotreeServiceImpl votreeServiceImpl;
  
  VotingMapper votingMapper;
  String votreeId;
  String body;
  Voting voting;
  
  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    votingMapper = mock(VotingMapper.class);
    votreeId = "54129058205gk-15";
    when(dbShardingManager.getMapper(anyObject(), anyObject())).thenReturn(votingMapper);
    
    voting = new Voting();
    voting.setVotreeId("votree id");
    voting.setUserId(1);
    voting.setVotes(
        Arrays.asList(new Voting.VoteInVoting().setVoteId(1).setUserChoices(null),
//            new Voting.VoteInVoting().setVoteId(2).setUserChoices(Arrays.asList(11)),
            new Voting.VoteInVoting().setVoteId(3).setUserChoices(null)
            )
        );
  }
  
  @Test
  public void addUserChoiceTest_올바른값이_입력된_경우() {
    when(votingMapper.insertToVoteBox(any(Voting.class))).thenReturn(1);
    
    RestResponse resultResponse = votreeServiceImpl.addUserChoice(voting);
    assertTrue(resultResponse.getHeader().getIsSuccessful());
    assertThat(resultResponse.getHeader().getResultCode(),is(200));
    assertThat(resultResponse.getHeader().getResultMessage(),is("투표 생성 1개 성공"));
  }
  
  @Test()
  public void addUserChoiceTest_바디가_null인_경우() {
    when(votingMapper.insertToVoteBox(any(Voting.class))).thenReturn(1);
    votreeServiceImpl.addUserChoice(voting);
    RestResponse resultResponse = votreeServiceImpl.addUserChoice(null);
    assertFalse(resultResponse.getHeader().getIsSuccessful());
    assertThat(resultResponse.getHeader().getResultCode(),is(400));
    assertThat(resultResponse.getHeader().getResultMessage(),is("NullPointerException"));
  }
  
  @Test
  public void addUserChoiceTest_insert가_실패한_경우() {
    body = "{\"votreeId\":\"28\""
        + ",\"voteList\":[{\"voteId\":\"73\""
        + ",\"userChoice\":[\"138\"]}]}";
//    when(votingMapper.insertToVoteBox(any(Map.class))).thenThrow(Exception.class);
    votreeServiceImpl.addUserChoice(voting);
    RestResponse resultResponse = votreeServiceImpl.addUserChoice(voting);
    assertFalse(resultResponse.getHeader().getIsSuccessful());
    assertThat(resultResponse.getHeader().getResultCode(),is(400));
  }
  
  @Test
  public void removeAndAddUserChoiceTest_올바른값이_입력된_경우() {
    body = "{\"votreeId\":\"28\""
        + ",\"voteList\":[{\"voteId\":\"73\""
        + ",\"userChoice\":[\"138\"]}]}";
    
    when(votingMapper.insertToVoteBox(any(Voting.class))).thenReturn(1);
    
    RestResponse resultResponse = votreeServiceImpl.updateUserChoice(voting);
    assertTrue(resultResponse.getHeader().getIsSuccessful());
    assertThat(resultResponse.getHeader().getResultCode(),is(200));
    assertThat(resultResponse.getHeader().getResultMessage(),is("재투표 생성 1개 성공"));
  }
  
  @Test()
  public void removeAndAddUserChoiceTest_바디가_null인_경우() {
    when(votingMapper.insertToVoteBox(any(Voting.class))).thenReturn(1);
    votreeServiceImpl.addUserChoice(voting);
    RestResponse resultResponse = votreeServiceImpl.updateUserChoice(null);
    assertFalse(resultResponse.getHeader().getIsSuccessful());
    assertThat(resultResponse.getHeader().getResultCode(),is(400));
    assertThat(resultResponse.getHeader().getResultMessage(),is("NullPointerException"));
  }
  
  @Test()
  public void removeAndAddUserChoiceTest_insert가_실패한_경우() {
    body = "{\"votreeId\":\"28\""
        + ",\"voteList\":[{\"voteId\":\"73\""
        + ",\"userChoice\":[\"138\"]}]}";
    when(votingMapper.insertToVoteBox(any(Voting.class))).thenReturn(0);
    RestResponse resultResponse = votreeServiceImpl.updateUserChoice(voting);
    assertFalse(resultResponse.getHeader().getIsSuccessful());
    assertThat(resultResponse.getHeader().getResultCode(),is(400));
    assertThat(resultResponse.getHeader().getResultMessage(),is("DB반영된 Row count is zero"));
  }
}
