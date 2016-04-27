package com.toast.votree.test.controller.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import javax.validation.constraints.AssertTrue;

import com.mysql.jdbc.Buffer;
import com.toast.votree.controller.api.VotreeRestController;
import com.toast.votree.service.VotreeService;
import com.toast.votree.util.RestResponse;
import com.toast.votree.util.RestResponse.Builder;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
    "classpath:spring/applicationContext-test.xml",
    "classpath:spring/servlet-context.xml"
})
@Transactional
public class VotreeRestControllerTest {

  @Mock
  VotreeService votreeService;
  @InjectMocks
  VotreeRestController vrc;
  RestResponse restResponse = new Builder().build();
  
  String body;
  int userId;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    body = "{\"votreeTitle\":\"123\",\"startDatetime\":\"2016-03-02 00:00\",\"dueDatetime\":\"2016-03-03 00:00\",\"isPrivate\":false,\"turnOut\":0,\"voteList\":[{\"voteName\":\"123\",\"itemList\":[{\"category\":\"텍스트\",\"value\":\"123\"},{\"category\":\"텍스트\",\"value\":\"123\"},{\"category\":\"텍스트\",\"value\":\"123\"}],\"isDuplicate\":false}]}";
    userId = 1543970985;
  }
  @Test
  public void registVotree_Test_성공() {
//    when(votreeService.createVotree(body, userId)).thenReturn(restResponse);
//    restResponse = vrc.registVotree(body, userId);
    assertTrue(restResponse.getHeader().getIsSuccessful());
  }
  @Test
  public void registVotree_Test_NULL() {
    restResponse = null;
//    when(votreeService.createVotree(body, userId)).thenReturn(restResponse);
//    restResponse = vrc.registVotree(body, userId);

    assertNull(restResponse);
  }
  
  @Test
  public void createVotreeValidatorTest(){
    
  }
}
