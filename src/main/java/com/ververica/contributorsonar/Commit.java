package org.apache.flink.streaming.examples.github;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonCreator;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class Commit implements WithEventTime {

    private final int deletionCount;
    private final int additionCount;

    private final String commitHash;
    private final String message;

    private final String authorName;
    private final String authorLogin;
    private final String authorEmail;
    private final String authorId;
    private final Instant authoredTimestamp;

    private final String committerName;
    private final String committerLogin;
    private final String committerEmail;
    private final String committerId;
    private final Instant committedTimestamp;

    @JsonCreator
    public Commit(
            @JsonProperty("deletions") int deletionCount,
            @JsonProperty("additions") int additionCount,
            @JsonProperty("oid") String commitHash,
            @JsonProperty("message") String message,
            @JsonProperty("author_name") String authorName,
            @JsonProperty("author_login") String authorLogin,
            @JsonProperty("author_email") String authorEmail,
            @JsonProperty("author_databaseId") String authorId,
            @JsonProperty("authorDate") Instant authoredTimestamp,
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
        return getCommittedTimestamp();
    }

    public int getDeletionCount() {
        return deletionCount;
    }

    public int getAdditionCount() {
        return additionCount;
    }

    public String getCommitHash() {
        return commitHash;
    }

    public String getMessage() {
        return message;
    }

    public Instant getCommittedTimestamp() {
        return committedTimestamp;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getAuthorLogin() {
        return authorLogin;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public String getAuthorId() {
        return authorId;
    }

    public Instant getAuthoredTimestamp() {
        return authoredTimestamp;
    }

    public String getCommitterName() {
        return committerName;
    }

    public String getCommitterLogin() {
        return committerLogin;
    }

    public String getCommitterEmail() {
        return committerEmail;
    }

    public String getCommitterId() {
        return committerId;
    }
}
