package com.toast.votree.test.controller.view;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.ParseException;

import javax.servlet.http.Cookie;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.toast.votree.service.SessionRedisService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/applicationContext-test.xml",
    "classpath:spring/servlet-context.xml" })
@WebAppConfiguration
public class ProfileControllerTest {
  @Autowired
  private WebApplicationContext wac;

  @Autowired
  SessionRedisService redisService;

  private MockMvc mockMvc;
  Cookie[] cookies;
  Cookie cookie;

  @Before
  public void setup() throws ParseException {
    MockitoAnnotations.initMocks(this);
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    cookies = new Cookie[1];
    cookie = new Cookie("VTSESSION", "asfsdf");
    cookies[0] = cookie;
  }

  @Test
  public void testshowProfilePage() throws Exception {
//    mockMvc.perform(get("/profiles/{userId}", 1).param("modifiedName", "").cookie(cookies))
//        .andExpect(model().attributeExists("user")).andExpect(model().attributeExists("runningVotrees"))
//        .andExpect(model().attributeExists("expiredVotrees")).andExpect(forwardedUrl("/WEB-INF/views/profile.jsp"))
//        .andExpect(status().isOk());
  }
}