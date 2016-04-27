package com.toast.votree.test.controller.view;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.ParseException;

import javax.servlet.http.Cookie;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.toast.votree.service.SessionRedisService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
    "classpath:spring/applicationContext.xml",
    "classpath:spring/servlet-context.xml"
})
@WebAppConfiguration
public class DetailVotreeControllerTest {
  @Autowired
  private WebApplicationContext wac;
  
  
  @Autowired
  SessionRedisService redisService;
  
  private MockMvc mockMvc;
  
  @Before
  public void setup() throws ParseException {
      this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
  }
  
  @Test
  @Ignore
  public void testShowVotreeDetail_올바른_PATH_VARIABLE() throws Exception{
    final String DEFAULT_VALUE = "";
    Cookie cookie = new Cookie("VTSESSION", "RANDOM_STRING");
    mockMvc.perform(get("/detail/1456079611828_417a76e290f449a3aeab8bfcd4d1d28e")
            .cookie(cookie)
            .param("votreeHiddenInput", DEFAULT_VALUE)
      ).andDo(print())
      .andExpect(status().isOk())
      .andExpect(forwardedUrl("/WEB-INF/views/detail.jsp"))
      .andExpect(model().attributeExists("votree"))
      .andExpect(model().attributeExists("voteList"));
  }
  
  @Test
  public void testShowVotreeDetail_올바르지않은_PATH_VARIABLE() throws Exception{
    final String DEFAULT_VALUE = "";
    mockMvc.perform(get("/detail/1456079611828_417a76e290f449a3aeab8bfcd4d1d28e/vote")
            .param("votreeHiddenInput", DEFAULT_VALUE)
      ).andDo(print())
      .andExpect(status().isNotFound());
  }
  
  @After
  public void deleteRedisKey(){
    redisService.delete("RANDOM_STRING");
  }
  
}
