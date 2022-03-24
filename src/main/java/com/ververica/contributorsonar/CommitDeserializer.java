package com.ververica.contributorsonar;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class CommitDeserializer implements Deserializer<Commit> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public Commit deserialize(String line) throws IOException {
        return OBJECT_MAPPER.readValue(line, Commit.class);
    }
}
