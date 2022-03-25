package com.ververica.contributorsonar;

import java.io.Serializable;
import java.time.Instant;

public class AwardCommit implements Serializable {
  public final CommitterLevel committerLevel;
  public final String authorName;
  public final Instant authoredDate;

  public AwardCommit(CommitterLevel committerLevel, String authorName, Instant authoredDate) {
    this.committerLevel = committerLevel;
    this.authorName = authorName;
    this.authoredDate = authoredDate;
  }

  @Override
  public String toString() {
    String date =
        String.format(
            "%d-%d", TimeUtils.extractYear(authoredDate), TimeUtils.extractMonth(authoredDate));
    if (committerLevel.equals(CommitterLevel.FIRST)) {
      return String.format("%s A wild %s appears", date, authorName, committerLevel);
    } else {
      return String.format("%s %s reached %s!", date, authorName, committerLevel);
    }
  }
}
