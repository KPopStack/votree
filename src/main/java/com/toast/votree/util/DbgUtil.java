package com.toast.votree.util;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbgUtil {
  private static final Logger logger = LoggerFactory.getLogger(DbgUtil.class);

  static public Logger logger() {
    return logger;
  }
  
  static public String dump(Object obj) {
    return "[DUMP:"+getCallerClassName()+"] "+ToStringBuilder.reflectionToString(obj, ToStringStyle.MULTI_LINE_STYLE);
  }

  static public String dumpShortStyle(Object obj) {
    return "[DUMP:"+getCallerClassName()+"] "+ToStringBuilder.reflectionToString(obj, ToStringStyle.SHORT_PREFIX_STYLE);
  }

  public static String getCallerClassName() { 
    StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
    for (int i=1; i<stElements.length; i++) {
      StackTraceElement ste = stElements[i];
      if (!ste.getClassName().equals(DbgUtil.class.getName()) && ste.getClassName().indexOf("java.lang.Thread")!=0) {
        return ste.getClassName();
      }
    }
    return null;
  }
}
