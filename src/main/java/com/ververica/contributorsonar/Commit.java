package com.ververica.contributorsonar;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class Commit implements WithEventTime {

  public Integer deletionCount;
  public Integer additionCount;

  public String commitHash;
  public String message;

  public String authorName;
  public String authorLogin;
  public String authorEmail;
  public String authorId;

  public Instant authoredTimestamp;

  public String committerName;
  public String committerLogin;
  public String committerEmail;
  public String committerId;

  public Instant committedTimestamp;

  public Commit() {}

  @JsonCreator
  public Commit(
      @JsonProperty("deletions") Integer deletionCount,
      @JsonProperty("additions") Integer additionCount,
      @JsonProperty("oid") String commitHash,
      @JsonProperty("message") String message,
      @JsonProperty("author_name") String authorName,
      @JsonProperty("author_login") String authorLogin,
      @JsonProperty("author_email") String authorEmail,
      @JsonProperty("author_databaseId") String authorId,
      @JsonProperty("authoredDate") Instant authoredTimestamp,
      @JsonProperty("committer_name") String committerName,
      @JsonProperty("committer_login") String committerLogin,
      @JsonProperty("committer_email") String committerEmail,
      @JsonProperty("committer_databaseId") String committerId,
      @JsonProperty("committedDate") Instant committedTimestamp) {
    this.deletionCount = deletionCount;
    this.additionCount = additionCount;
    this.commitHash = commitHash;
    this.message = message;
    this.committedTimestamp = committedTimestamp;
    this.authorName = authorName;
    this.authorLogin = authorLogin;
    this.authorEmail = authorEmail;
    this.authorId = authorId;
    this.authoredTimestamp = authoredTimestamp;
    this.committerName = committerName;
    this.committerLogin = committerLogin;
    this.committerEmail = committerEmail;
    this.committerId = committerId;
  }

  @Override
  public Instant getEventTime() {
    return authoredTimestamp;
  }

  @Override
  public String getKey() {
    return this.authorLogin;
  }
}
