package com.toast.votree.test.controller.view;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import com.toast.votree.config.RestServer;
import com.toast.votree.controller.view.VotingController;
import com.toast.votree.service.SessionRedisService;
import com.toast.votree.vo.Votree;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
    "classpath:spring/applicationContext.xml",
    "classpath:spring/servlet-context.xml"
})
@WebAppConfiguration
public class HomeControllerTest {
  
  @Autowired
  private WebApplicationContext wac;
  @Autowired
  SessionRedisService redisService;
  
  private MockMvc mockMvc;

  @Mock
  RedisTemplate<String, Object> redisTemplate;
  @Mock
  RestServer restServer;
  @InjectMocks
  VotingController votingController;
  @Before
  public void setup() throws ParseException {
      this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
  }

  @Test
  @Ignore
  public void testshowMainPage() throws Exception {
    final String DEFAULT_VALUE = "0";
    mockMvc.perform(get("/")
            .param("runningPageNum", DEFAULT_VALUE)
            .param("expiredPageNum", DEFAULT_VALUE)
            .param("voteListType", DEFAULT_VALUE)
      ).andDo(print())
      .andExpect(status().isOk())
      .andExpect(forwardedUrl("/WEB-INF/views/home.jsp"))
      .andExpect(model().attributeExists("runningVotrees"))
      .andExpect(model().attributeExists("expiredVotrees"))
      .andExpect(model().attributeExists("runningPageNum"))
      .andExpect(model().attributeExists("expiredPageNum"))
      .andExpect(model().attributeExists("contentsPerPage"))
      .andExpect(model().attributeExists("voteListType"));
  }
  
  @Test
  public void logoutTest() throws Exception {
    Cookie cookie = new Cookie("VTSESSION", "RANDOM_STRING");
    redisService.expire("RANDOM_STRING", 30, TimeUnit.SECONDS);
    redisService.opsHashForPutUser("RANDOM_STRING", 9999, "USERNAME");
    
    mockMvc.perform(get("/logout")
        .cookie(cookie)
      ).andDo(print())
      .andExpect(status().is3xxRedirection());
  }
  
  @After
  public void deleteRedisKey(){
    redisService.delete("RANDOM_STRING");
  }
}
