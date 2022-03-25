package com.ververica.contributorsonar;

import java.io.Serializable;
import java.time.Instant;

public class AwardStreak implements Serializable {
  public final String authorName;
  public final Instant authoredDate;
  public final Integer length;

  public AwardStreak(String authorName, Instant authoredDate, Integer length) {
    this.authorName = authorName;
    this.authoredDate = authoredDate;
    this.length = length;
  }

  @Override
  public String toString() {
    return String.format(
        "%d-%d %s has maintained a streak of %d weeks",
        TimeUtils.extractYear(authoredDate),
        TimeUtils.extractMonth(authoredDate),
        authorName,
        length);
  }
}
