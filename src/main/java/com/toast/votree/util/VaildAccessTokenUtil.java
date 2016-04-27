package com.toast.votree.util;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class VaildAccessTokenUtil {
  public static Timestamp getCurrentTimeStamp()
  {
    Instant nowTimeInstant = Instant.now();
    ZonedDateTime nowZoneDateTime = ZonedDateTime.ofInstant(nowTimeInstant, ZoneId.of("Z"));
    Timestamp currentTimeStamp = Timestamp.from(nowZoneDateTime.plusMinutes(5).toInstant());
    return currentTimeStamp;
  }
}
