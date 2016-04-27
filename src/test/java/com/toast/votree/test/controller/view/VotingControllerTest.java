package com.toast.votree.test.controller.view;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriComponentsBuilder;

import com.toast.votree.config.RestServer;
import com.toast.votree.config.ToastCloud;
import com.toast.votree.controller.view.VotingController;
import com.toast.votree.interceptor.VotingInterceptor;
import com.toast.votree.oauthinfo.Facebook;
import com.toast.votree.service.SessionRedisService;
import com.toast.votree.util.CookieUtil;
import com.toast.votree.util.DbgUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
    "classpath:spring/applicationContext-test.xml",
    "classpath:spring/servlet-context.xml"
})
@WebAppConfiguration
public class VotingControllerTest {
  @Autowired
  private WebApplicationContext wac;
  private MockMvc mockMvc;
  
  @Autowired
  SessionRedisService redisService;
  
  Cookie cookie;
  Cookie cookies[];
  
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    
    cookies = new Cookie[1];
    cookie = new Cookie("VTSESSION", "REDIS_KEY_FOR_TEST");
    cookies[0] = cookie;
    
  }
  
  @Test
  public void displayVotingPage_test_로그인_했을때() throws Exception {
    redisService.createDataByKeyInHashKey("REDIS_KEY_FOR_TEST", "userId", 1);
    //TOD0 VotingController의 votreeId값..
    mockMvc.perform(get("/voting/{votreeId}/{voteMode}","1457062854241_7f0f6a8ab52644079dc6f1eec71dec1f","vote")
        .cookie(cookies))
//      .andDo(print())
      .andExpect(status().is2xxSuccessful());
  }
  @Test
  public void displayVotingPage_test_로그인_안했을때() throws Exception {
    
    mockMvc.perform(get("/voting/{votreeId}/{voteMode}","123","vote")
            .cookie(cookies))
          .andExpect(status().is3xxRedirection());
  }
  
  @After
  public void initRedis(){
    cookie.setMaxAge(0);
    redisService.delete("REDIS_KEY_FOR_TEST");
  }
}
