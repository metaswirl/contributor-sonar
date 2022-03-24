package org.apache.flink.streaming.examples.github;

import java.time.Instant;

public interface WithEventTime {

    Instant getEventTime();
}
