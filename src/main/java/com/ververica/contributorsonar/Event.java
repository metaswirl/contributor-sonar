package com.ververica.contributorsonar;

import java.io.Serializable;

public class Event implements Serializable {
  public final CommitterLevel committerLevel;
  public final String authorName;

  public Event(CommitterLevel committerLevel, String authorName) {
    this.committerLevel = committerLevel;
    this.authorName = authorName;
  }
}
