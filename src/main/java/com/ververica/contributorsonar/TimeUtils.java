package com.ververica.contributorsonar;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class TimeUtils {
  public static int extractWeekInYear(Instant timestamp) {
    return LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault())
        .atZone(ZoneId.systemDefault())
        .get(WeekFields.of(Locale.getDefault()).weekOfYear());
  }

  public static int extractYear(Instant timestamp) {
    return LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault())
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .getYear();
  }

  public static int extractMonth(Instant timestamp) {
    return LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault())
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .getMonthValue();
  }
}
