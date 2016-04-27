package com.toast.votree.controller.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.toast.votree.config.ToastCloud;

@Controller
public class FileStorageController {

  @Autowired
  ToastCloud toastCloud;
  @Autowired
  RestTemplate restTemplate;

  private static final String FILE_SEPARATOR = System.getProperty("file.separator"); 

  //TODO REVIEW
  @RequestMapping(value = "/files/*", method = RequestMethod.GET)
  public @ResponseBody Object getBinaryImage(HttpServletRequest request, HttpServletResponse response) {
    response.setContentType("");
    String fileName = request.getServletPath().substring(6);
    final String REQUEST_URL_TO_GET_OBJECT = 
        toastCloud.getRequestUrlToGetObject()
        + toastCloud.getAccount()
        + toastCloud.getPathForImages()
        + fileName;

    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(REQUEST_URL_TO_GET_OBJECT);
    ResponseEntity<byte[]> responseEntity = restTemplate.getForEntity(uriBuilder.build().toUri(), byte[].class);
    response.setContentType("image/png");
    return responseEntity;
  }

//  @RequestMapping( "/file/{fileName}/download" )
//  public ResponseEntity downloadLogFile(@PathVariable final String fileName) {
//    HttpHeaders headers = new HttpHeaders();
//    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//    final String REQUEST_URL_TO_GET_OBJECT = 
//        toastCloud.getRequestUrlToGetObject()
//        + toastCloud.getAccount()
//        + toastCloud.getPathForImages()
//        + URLEncoder.encode( fileName, Charsets.UTF_8.toString());
//
//    // request whole file content from the server
//    try {
//      RestTemplate restTemplate = new RestTemplate();
//      SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
//      requestFactory.setBufferRequestBody(false);     
//      restTemplate.setRequestFactory(requestFactory);     
//
//      RequestCallback requestCallback = new RequestCallback() {
//        @Override
//        public void doWithRequest(ClientHttpRequest request)
//            throws IOException {
//          DbgUtil.logger().debug("Downloading log file: "  + fileName );
//        }
//      };
//
//      HttpMessageConverterExtractor responseExtractor =
//          new HttpMessageConverterExtractor(byte[].class, restTemplate.getMessageConverters());
//
//      byte[] responseBytes = restTemplate.execute( requestURL, HttpMethod.GET, requestCallback, responseExtractor);
//
//      long contentLength = responseBytes != null ?  responseBytes.length : 0;
//      headers.setContentLength((int)contentLength);
//      headers.setContentDispositionFormData("inline", URLEncoder.encode( fileName, Charsets.UTF_8.toString()));
//      return new ResponseEntity( responseBytes, headers, HttpStatus.OK );
//
//
//    } catch ( Exception e ) {
//      logger.error(e.getMessage());
//    }
//
//    return new ResponseEntity(headers, HttpStatus.BAD_REQUEST);
//
//  }


}
