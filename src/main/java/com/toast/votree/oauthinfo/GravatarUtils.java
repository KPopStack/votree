package com.toast.votree.oauthinfo;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.toast.votree.util.DbgUtil;

public class GravatarUtils {
  public static String getUrl(String email) {
    try {
      MessageDigest messagedigest = MessageDigest.getInstance("MD5");
      return emailToHex (messagedigest.digest(email.getBytes("CP1252")));
    } catch (NoSuchAlgorithmException e) {
      DbgUtil.logger().error(e.getLocalizedMessage());
    } catch (UnsupportedEncodingException e) {
      DbgUtil.logger().error(e.getLocalizedMessage());
    }
    return null;
  }

  private static String emailToHex(byte[] digest) {
    StringBuffer stringbuffer = new StringBuffer();
    for (int i = 0; i < digest.length; ++i) {
      stringbuffer.append(Integer.toHexString((digest[i]
          & 0xFF) | 0x100).substring(1,3));        
    }
    return stringbuffer.toString();
  }
}