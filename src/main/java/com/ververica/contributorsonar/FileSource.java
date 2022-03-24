package com.ververica.contributorsonar;

import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.util.Preconditions;

import javax.annotation.Nullable;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.zip.GZIPInputStream;

public class FileSource<T extends WithEventTime> implements SourceFunction<T> {

  @Nullable private Instant startTime;
  private final int servingSpeed;

  private final Deserializer<T> deserializer;
  private BufferedReader reader;

  public FileSource(String dataFilePath, Deserializer<T> deserializer) throws IOException {
    this(dataFilePath, deserializer, null, 1);
  }

  public FileSource(
      String dataFilePath,
      Deserializer<T> deserializer,
      @Nullable Instant startTime,
      int servingSpeedFactor)
      throws IOException {
    this.deserializer = deserializer;
    this.reader =
        new BufferedReader(
            new InputStreamReader(new GZIPInputStream(new FileInputStream(dataFilePath))));
    this.startTime = startTime;
    this.servingSpeed = servingSpeedFactor;
  }

  @Override
  public void run(SourceContext<T> sourceContext) throws Exception {
    loadData(sourceContext);
  }

  private void loadData(SourceContext<T> sourceContext) throws IOException, InterruptedException {
    String line = reader.readLine();
    while (line != null) {
      final T entity = deserializer.deserialize(line);
      initializeStart(entity);
      if (entity.getEventTime().isBefore(Objects.requireNonNull(startTime))) {
        continue;
      }

      final long waitTime = waitTimeTill(entity.getEventTime());
      Thread.sleep(waitTime);

      sourceContext.collect(entity);

      line = reader.readLine();
    }
  }

  private void initializeStart(T entity) {
    if (this.startTime == null) {
      this.startTime = entity.getEventTime();
    }
  }

  private Duration sinceStart(Instant end) {
    Preconditions.checkState(startTime != null, "Start time is not initialized.");
    return Duration.between(startTime, end);
  }

  private Duration currentSimulatedTime() {
    return sinceStart(Instant.now());
  }

  private Duration waitDuration(Instant timestamp) {
    final Duration tillTimestamp = sinceStart(timestamp);
    final Duration nowDuration = currentSimulatedTime();

    return tillTimestamp.minus(nowDuration);
  }

  private long simulatedDurationInMillis(Duration actualDuration) {
    return actualDuration.toMillis() / servingSpeed;
  }

  private long waitTimeTill(Instant timestamp) {
    final Duration realWaitTime = waitDuration(timestamp);
    return Math.max(0L, simulatedDurationInMillis(realWaitTime));
  }

  @Override
  public void cancel() {
    try {
      if (this.reader != null) {
        this.reader.close();
      }
    } catch (IOException ioe) {
      throw new RuntimeException("Could not cancel SourceFunction", ioe);
    } finally {
      this.reader = null;
    }
  }
}
