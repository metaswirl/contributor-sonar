package org.apache.flink.streaming.examples.github;

import java.io.IOException;

public interface Deserializer<T extends WithEventTime> {

    T deserialize(String line) throws IOException;
}
