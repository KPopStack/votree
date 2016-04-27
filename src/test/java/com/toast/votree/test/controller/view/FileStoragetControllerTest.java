package com.toast.votree.test.controller.view;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

import com.toast.votree.config.ToastCloud;
import com.toast.votree.controller.view.FileStorageController;
import com.toast.votree.oauthinfo.Payco;
import com.toast.votree.util.CookieUtil;

import static org.mockito.Matchers.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@ContextConfiguration(locations = {
    "classpath:spring/applicationContext.xml",
    "classpath:spring/servlet-context.xml"
})
@WebAppConfiguration
@PrepareForTest(UriComponentsBuilder.class)
public class FileStoragetControllerTest {
  
  @Mock
  MockHttpServletResponse mockResponse;
  @Mock
  MockHttpServletRequest mockRequest;
  @Mock
  RestTemplate restTemplate;

  @Mock
  ToastCloud toastCloud;
  @InjectMocks
  FileStorageController fileStorageController;
  
  @Before
  public void setup() throws ParseException {
    MockitoAnnotations.initMocks(this);
  }
  
  @Test
  public void testGetBinaryImage() throws Exception{
    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl("http://abc.abc");
    mockStatic(UriComponentsBuilder.class);
    ResponseEntity<byte[]> responseEntity = null;
    PowerMockito.when(restTemplate.getForEntity(Mockito.any(URI.class), eq(byte[].class))).thenReturn(responseEntity);
    Mockito.when(mockRequest.getServletPath()).thenReturn("filename");
    PowerMockito.when(UriComponentsBuilder.fromHttpUrl(anyString())).thenReturn(uriBuilder);
  }
}
