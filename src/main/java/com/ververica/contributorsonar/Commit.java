package com.ververica.contributorsonar;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class Commit implements WithEventTime {

    private final int deletionCount;
    private final int additionCount;

    private final String commitHash;
    private final String message;

    private final String authorName;
    private final String authorLogin;
    private final String authorEmail;
    private final String authorId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ssZ")
    private final Date authoredTimestamp;

    private final String committerName;
    private final String committerLogin;
    private final String committerEmail;
    private final String committerId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy'T'hh:mm:ssZ")
    private final Date committedTimestamp;

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
            @JsonProperty("authoredDate") Date authoredTimestamp,
            @JsonProperty("committer_name") String committerName,
            @JsonProperty("committer_login") String committerLogin,
            @JsonProperty("committer_email") String committerEmail,
            @JsonProperty("committer_databaseId") String committerId,
            @JsonProperty("committedDate") Date committedTimestamp) {
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
    public Date getEventTime() {
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

    public Date getCommittedTimestamp() {
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

    public Date getAuthoredTimestamp() {
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
