package com.toast.votree.test.controller.api;

import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.context.ContextConfiguration;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.toast.votree.controller.api.SearchRestController;
import com.toast.votree.service.UserService;
import com.toast.votree.service.VotreeService;
import com.toast.votree.vo.User;
import com.toast.votree.vo.Votree;
@RunWith(PowerMockRunner.class)
@ContextConfiguration(locations = {
    "classpath:spring/applicationContext-test.xml",
    "classpath:spring/servlet-context.xml"
})
@PrepareForTest({URLDecoder.class})
public class SearchRestControllerTest {

  @Mock
  UserService userService;
  @Mock
  VotreeService votreeService;
  
  @InjectMocks
  SearchRestController searchRestController;
  String query;
  String votreeName;
  Integer offset;
  List<User> searchUser;
  List<Votree> searchVotree;
  Map<String, Object> Votree;
  @Before
  public void setUp() throws JsonParseException, JsonMappingException, IOException {
    
    query="LeeD";
    votreeName="LeeD";
    offset = 1;
    searchUser = new ArrayList<>();
    
    searchUser.add(new User()
    .setId(1)
    .setEmail("Test@nhnent.com")
    .setCreatedAt(null)
    .setName("DongJu")
    .setOauthProvider("payco")
    .setUpdatedAt(null)
    .setProviderKey("abc123"));
   
    searchVotree = new ArrayList<>();
    searchVotree.add(new Votree()
        .setDueDatetime(null)
        .setHit(1)
        .setId("1")
        .setProposerId(1)
        .setProposerName("LeeDong")
        .setStartDatetime(null)
        .setTitle("Test")
        .setTurnout(1)
        .setType(1)
        .setVotingCount(1));
        
    
    MockitoAnnotations.initMocks(this);
   
  }
  @Test
  public void getSearchedUser_성공() throws UnsupportedEncodingException {
    mockStatic(URLDecoder.class);
    PowerMockito.when(URLDecoder.decode(query,"UTF-8")).thenReturn(query);
    when(userService.findUsersByNameOrEmail(URLDecoder.decode(query,"UTF-8"))).thenReturn(searchUser);
    searchUser = searchRestController.getSearchedUser(query);
    assertThat(searchUser.get(0).getId(), is(1));
    assertThat(searchUser.get(0).getEmail(), is("Test@nhnent.com"));
    assertThat(searchUser.get(0).getName(), is("DongJu"));
  }
  
  @Test(expected = UnsupportedEncodingException.class)
  public void getSearchedUser_실패() throws UnsupportedEncodingException {
    mockStatic(URLDecoder.class);
    PowerMockito.when(URLDecoder.decode(query,"UTF-8")).thenThrow(new UnsupportedEncodingException());
    when(userService.findUsersByNameOrEmail(URLDecoder.decode(query,"UTF-8"))).thenReturn(searchUser);
    searchUser = searchRestController.getSearchedUser(query);
    assertNull(searchUser);
  }
  
  @Test
  public void searchVotreeList() throws UnsupportedEncodingException {
    mockStatic(URLDecoder.class);
    PowerMockito.when(URLDecoder.decode(votreeName,"UTF-8")).thenReturn(query);
    when(votreeService.searchVotrees(URLDecoder.decode(votreeName,"UTF-8" ), offset)).thenReturn(searchVotree);
    searchVotree = searchRestController.searchVotreeList(votreeName, offset);
    assertThat(searchVotree.get(0).getId(),is("1"));
    assertThat(searchVotree.get(0).getProposerId(),is(1));
    assertThat(searchVotree.get(0).getTitle(),is("Test"));
    assertThat(searchVotree.get(0).getProposerName(),is("LeeDong"));
  }
  
  @Test(expected = UnsupportedEncodingException.class)
  public void searchVotreeList_실패() throws UnsupportedEncodingException {
    mockStatic(URLDecoder.class);
    PowerMockito.when(URLDecoder.decode(votreeName,"UTF-8")).thenThrow(new UnsupportedEncodingException());
    when(votreeService.searchVotrees(URLDecoder.decode(votreeName,"UTF-8"), offset)).thenReturn(searchVotree);
    searchVotree = searchRestController.searchVotreeList(votreeName, offset);
    assertNull(searchVotree);
  }
}