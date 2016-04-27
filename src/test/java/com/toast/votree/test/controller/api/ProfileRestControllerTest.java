package com.toast.votree.test.controller.api;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.toast.votree.controller.api.ProfileRestController;
import com.toast.votree.service.UserService;
import com.toast.votree.util.JsonUtil;
import com.toast.votree.util.RestResponse;
import com.toast.votree.vo.User;

@RunWith(MockitoJUnitRunner.class)
public class ProfileRestControllerTest {
  private MockMvc mockMvc;

  @Mock
  UserService userService;
  
  @InjectMocks
  private ProfileRestController profileRestController;

  User user;
  List<User> users;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    this.mockMvc = MockMvcBuilders.standaloneSetup(profileRestController).build();

    user = new User().setId(1).setEmail("seongwon.kong@nhnent.com").setName("KongSeongWon");
  }

  @Test
  public void testShowProfile() throws Exception {
    when(userService.findUserById(anyInt())).thenReturn(user);

    mockMvc.perform(get("/api/v0.1/users/{userId}", 1)).andDo(print())
    .andExpect(status().isOk())
    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
    .andExpect(jsonPath("$.id").value(user.getId()))
    .andExpect(jsonPath("$.name").value("KongSeongWon"));
  }
  
  @Test
  public void modifiedProfile() throws Exception {
    RestResponse ret = new RestResponse.Builder("프로필 변경 성공").body(user.getName()).build();
    when(userService.modifyUserProfile(anyInt(), anyString(), anyString())).thenReturn(ret);
    
    mockMvc.perform(put("/api/v0.1/users/{userId}", 1)
        .content("{\"email\":\"test@test.com\", \"name\":\"tester\"}")
        .contentType(MediaType.APPLICATION_JSON_UTF8))
    .andDo(print())
    .andExpect(status().isOk())
    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
    .andExpect(jsonPath("$.header.resultCode").value(200));
    
  }

  @Test
  public void testShowProfile_error() throws Exception {
    when(userService.findUserById(anyInt())).thenThrow(new RuntimeException());
    RestResponse ret = new RestResponse.Builder(false, 400, "이름 또는 이메일이 올바르지 않습니다.").build();
    when(userService.modifyUserProfile(anyInt(), anyString(), anyString())).thenReturn(ret);

    mockMvc.perform(put("/api/v0.1/users/{userId}", 1)
        .content("{\"email\":\"test@test.com\", \"name\":\"tester\"}")
        .contentType(MediaType.APPLICATION_JSON_UTF8))
    .andDo(print())
    .andExpect(jsonPath("$.header.resultCode").value(400));
  }

}
