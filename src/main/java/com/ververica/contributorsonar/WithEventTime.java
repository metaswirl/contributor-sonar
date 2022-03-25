package com.ververica.contributorsonar;

import java.time.Instant;

public interface WithEventTime<T> {

  Instant getEventTime();

  T getKey();
}
