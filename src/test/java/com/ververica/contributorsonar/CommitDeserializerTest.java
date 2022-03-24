package com.ververica.contributorsonar;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

public class CommitDeserializerTest {

  @Test
  public void testDeserialization() throws IOException {
    final DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
    final String actualMessage = "message";
    final Instant commitDate = Instant.now();
    final Instant authoredDate = Instant.now();
    final String oid = "3d62a652622cede5f094068d8af3ac591c59c0de";
    final String json =
        String.format(
            "{\"message\": \"%s\", \"committedDate\": \"%s\", \"authoredDate\": \"%s\", \"oid\": \"%s\", \"deletions\": 12, \"additions\": 0, \"author_email\": \"chesnay@apache.org\", \"author_name\": \"Chesnay Schepler\", \"author_login\": \"zentol\", \"author_databaseId\": 5725237, \"committer_email\": \"chesnay@apache.org\", \"committer_name\": \"Chesnay Schepler\", \"committer_login\": \"zentol\", \"committer_databaseId\": 5725237}",
            actualMessage, formatter.format(commitDate), formatter.format(authoredDate), oid);

    final CommitDeserializer testInstance = new CommitDeserializer();

    final Commit deserializedEntity = testInstance.deserialize(json);

    assertThat(deserializedEntity.message).isEqualTo(actualMessage);
    assertThat(deserializedEntity.committedTimestamp).isEqualTo(commitDate);
    assertThat(deserializedEntity.authoredTimestamp).isEqualTo(authoredDate);
    assertThat(deserializedEntity.commitHash).isEqualTo(oid);
  }
}
