package com.ververica.contributorsonar;

import java.time.Instant;

public interface WithEventTime {

  Instant getEventTime();
}
