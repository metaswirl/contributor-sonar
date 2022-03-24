package com.ververica.contributorsonar;

import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FileSourceTest {

  @TempDir File temporaryDirectory;

  private static class TestEntry implements WithEventTime {

    private static final Instant base = Instant.now();

    private long diffInMs;

    public TestEntry(long diffInMs) {
      this.diffInMs = diffInMs;
    }

    @Override
    public Instant getEventTime() {
      return base.plus(diffInMs, ChronoUnit.MILLIS);
    }
  }

  private static class TestEntryDeserializer implements Deserializer<TestEntry> {

    @Override
    public TestEntry deserialize(String line) {
      return new TestEntry(Long.parseLong(line));
    }
  }

  @Test
  public void testEmitting() throws Exception {
    File sourceFile = createFile("500", "1000", "1200", "1700");
    List<Long> sleepTimes = new ArrayList<>();
    final FileSource<TestEntry> source =
        new FileSource<>(
            sourceFile.getAbsolutePath(), new TestEntryDeserializer(), null, 1, sleepTimes::add);

    source.open(null);

    TestingSourceContext sourceContext = new TestingSourceContext();
    source.run(sourceContext);

    assertThat(sleepTimes).containsExactly(0L, 500L, 200L, 500L);
  }

  @Test
  public void testSerializing() throws IOException {
    String filePath = System.getProperty("user.dir") + "/data/commits-test.txt";
    FileSource<Commit> source = new FileSource<>(filePath, new CommitDeserializer(), 1000);
    new ObjectOutputStream(new ByteArrayOutputStream()).writeObject(source);
  }

  private static class TestingSourceContext implements SourceFunction.SourceContext<TestEntry> {

    @Override
    public void collect(TestEntry testEntry) {}

    @Override
    public void collectWithTimestamp(TestEntry testEntry, long l) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void emitWatermark(Watermark watermark) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void markAsTemporarilyIdle() {
      throw new UnsupportedOperationException();
    }

    @Override
    public Object getCheckpointLock() {
      throw new UnsupportedOperationException();
    }

    @Override
    public void close() {
      throw new UnsupportedOperationException();
    }
  }

  private File createFile(String... lines) throws IOException {
    final File sourceFile =
        new File(temporaryDirectory, "random-source-file-" + System.currentTimeMillis());
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(sourceFile))) {
      for (String line : lines) {
        writer.write(line);
        writer.newLine();
      }
    }

    return sourceFile;
  }
}
