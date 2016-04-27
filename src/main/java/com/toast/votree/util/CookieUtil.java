package com.toast.votree.util;

import javax.servlet.http.Cookie;

public class CookieUtil {
  
  public static Cookie getCookieByName(Cookie[] cookies, String name){
	 if (cookies == null) {
	  return null;
	 }
    for(Cookie cookie : cookies){
      if(cookie.getName().equals(name)){
        return cookie;
      }
    }
    return null;
  }
}
