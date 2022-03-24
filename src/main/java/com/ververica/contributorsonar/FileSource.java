package com.ververica.contributorsonar;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import javax.annotation.Nullable;
import java.io.*;
import java.time.Duration;
import java.time.Instant;

public class FileSource<T extends WithEventTime> extends RichSourceFunction<T>
    implements SourceFunction<T>, Serializable {

  @Nullable private Instant lastEventTime;
  private final int servingSpeed;

  private final Deserializer<T> deserializer;
  private final String dataFilePath;

  private BufferedReader reader;

  private final MilliSecondSleeper sleeper;

  public FileSource(String dataFilePath, Deserializer<T> deserializer, int servingSpeedFactor) {
    this(dataFilePath, deserializer, null, servingSpeedFactor, Thread::sleep);
  }

  public FileSource(
      String dataFilePath,
      Deserializer<T> deserializer,
      @Nullable Instant startTime,
      int servingSpeedFactor) {
    this(dataFilePath, deserializer, startTime, servingSpeedFactor, Thread::sleep);
  }

  public FileSource(
      String dataFilePath,
      Deserializer<T> deserializer,
      @Nullable Instant startTime,
      int servingSpeedFactor,
      MilliSecondSleeper sleeper) {
    this.deserializer = deserializer;
    this.dataFilePath = dataFilePath;
    this.lastEventTime = startTime;
    this.servingSpeed = servingSpeedFactor;
    this.sleeper = sleeper;
  }

  @Override
  public void open(Configuration parameters) throws Exception {
    this.reader = new BufferedReader(new InputStreamReader(new FileInputStream(dataFilePath)));
    super.open(parameters);
  }

  @Override
  public void run(SourceContext<T> sourceContext) throws Exception {
    String line = reader.readLine();
    while (line != null) {
      final T entity = deserializer.deserialize(line);

      final long waitTime =
          lastEventTime == null
              ? 0
              : Duration.between(lastEventTime, entity.getEventTime()).toMillis() / servingSpeed;
      sleeper.sleep(Math.max(waitTime, 0));

      sourceContext.collect(entity);

      lastEventTime = entity.getEventTime();

      line = reader.readLine();
    }
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

  interface MilliSecondSleeper extends Serializable {
    void sleep(long millis) throws InterruptedException;
  }
}
