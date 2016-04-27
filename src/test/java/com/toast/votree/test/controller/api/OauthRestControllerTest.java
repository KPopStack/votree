package com.toast.votree.test.controller.api;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;


import com.toast.votree.controller.api.OauthRestController;
import com.toast.votree.service.UserService;
import com.toast.votree.vo.User;


@RunWith(MockitoJUnitRunner.class)
public class OauthRestControllerTest {
  private MockMvc mockMvc;

  @Mock
  UserService userService;

  @InjectMocks
  OauthRestController controller;

  User registeredUser;
  
  @Before
  public void setUp() throws Exception{
    MockitoAnnotations.initMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    registeredUser = new User()
        .setId(1)
        .setName("tester")
        .setEmail("tester@test.com")
        .setOauthProvider("facebook")
        .setProviderKey("yf32fd");
  }

  @Test
  public void testInfomationStore_로그인경험O() throws Exception{
    when(userService.findOrSaveUserByOauth(anyString(), anyString(), anyString(), anyString()))
        .thenReturn(registeredUser);

    mockMvc.perform(get("/api/v0.1/users")
        .param("provider_key","123")
        .param("oauth_provider","facebook")
        .param("name", "dongdong"))
    .andDo(print())
    .andExpect(status().isOk())
    .andExpect(jsonPath("$").isMap())
    .andExpect(jsonPath("$.id").value(1));
    
  }

  @Test
  public void testInfomationStore_providerKey가없을때() throws Exception{
    mockMvc.perform(get("/api/v0.1/users")
        .param("oauth_provider","facebook")
        .param("name", "dongdong"))
    .andDo(print())
    .andExpect(status().is4xxClientError());
  }
}
