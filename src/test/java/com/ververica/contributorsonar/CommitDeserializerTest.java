package com.ververica.contributorsonar;

import org.junit.jupiter.api.Test;

import java.io.IOException;

public class CommitDeserializerTest {

    @Test
    public void testDeserialization() throws IOException {
        final String json =
                "{\"message\": \"[hotfix][docs][metrics] Remove outdated statements\", \"committedDate\": \"2022-03-24T13:46:28Z\", \"authoredDate\": \"2022-03-24T11:15:41Z\", \"oid\": \"3d62a652622cede5f094068d8af3ac591c59c0de\", \"deletions\": 12, \"additions\": 0, \"author_email\": \"chesnay@apache.org\", \"author_name\": \"Chesnay Schepler\", \"author_login\": \"zentol\", \"author_databaseId\": 5725237, \"committer_email\": \"chesnay@apache.org\", \"committer_name\": \"Chesnay Schepler\", \"committer_login\": \"zentol\", \"committer_databaseId\": 5725237}";

        final CommitDeserializer testInstance = new CommitDeserializer();

        final Commit deserializedEntity = testInstance.deserialize(json);

        System.out.println(deserializedEntity);
    }
}
