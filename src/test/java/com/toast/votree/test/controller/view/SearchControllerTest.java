package com.toast.votree.test.controller.view;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriComponentsBuilder;

import com.toast.votree.config.RestServer;
import com.toast.votree.controller.api.HomeRestController;
import com.toast.votree.controller.api.SearchRestController;
import com.toast.votree.controller.view.SearchController;
import com.toast.votree.util.DbgUtil;
import com.toast.votree.vo.User;
import com.toast.votree.vo.Votree;

@RunWith(PowerMockRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {
    "classpath:spring/applicationContext.xml",
    "classpath:spring/servlet-context.xml"
})

@PrepareForTest({UriComponentsBuilder.class,RestTemplate.class})
public class SearchControllerTest {

  @Mock
  RestServer restServer;
  @Mock
  RestTemplate restTemplate;
  @Mock
  MockHttpServletResponse mockResponse;
  @Mock
  MockHttpServletRequest mockRequest;

  @InjectMocks
  SearchController controller;

  private MockMvc mockMvc;
  String query;
  String hostName;
  int hostPort;
  List<Votree> votrees;
  List<User> users = new ArrayList<>();
  UriComponentsBuilder uriBuilder;
  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    query = "test";
    hostName = "test";
    hostPort = 9999;
    users = new ArrayList<>();
    users.add(new User()
        .setId(1)
        .setEmail("Test@nhnent.com")
        .setCreatedAt(null)
        .setName("DongJu")
        .setOauthProvider("payco")
        .setUpdatedAt(null)
        .setProviderKey("abc123"));
       
    votrees = new ArrayList<>();
    votrees.add(new Votree()
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
    
  }

  @Test
  public void testSearch_검색어_없을때() throws Exception {
    mockMvc.perform(get("/searff"))
    .andDo(print())
    .andExpect(status().isNotFound());
  }

  @SuppressWarnings("rawtypes")
  @Test
  public void testSearch_성공() throws Exception {
    mockStatic(RestTemplate.class);
    PowerMockito.when(restTemplate.getForObject(Mockito.any(URI.class), eq(ArrayList.class))).thenReturn((ArrayList) users);
    
    mockMvc.perform(get("/search")
        .param("query", query))
    .andDo(print())
    .andExpect(model().attributeExists("users"))
    .andExpect(model().attributeExists("votrees"))
    .andExpect(status().isOk());
    

  }


}
