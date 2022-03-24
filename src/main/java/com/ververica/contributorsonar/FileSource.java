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
import java.util.Date;
import java.util.Objects;
import java.util.zip.GZIPInputStream;

public class FileSource<T extends WithEventTime> implements SourceFunction<T> {

    @Nullable private Date startTime;
    private final int servingSpeed;

    private final Deserializer<T> deserializer;
    private BufferedReader reader;

    public FileSource(String dataFilePath, Deserializer<T> deserializer) throws IOException {
        this(dataFilePath, deserializer, null, 1);
    }

    public FileSource(
            String dataFilePath,
            Deserializer<T> deserializer,
            @Nullable Date startTime,
            int servingSpeedFactor)
            throws IOException {
        this.deserializer = deserializer;
        this.reader =
                new BufferedReader(
                        new InputStreamReader(
                                new GZIPInputStream(new FileInputStream(dataFilePath))));
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
            if (entity.getEventTime().before(Objects.requireNonNull(startTime))) {
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

    private Duration sinceStart(Date end) {
        Preconditions.checkState(startTime != null, "Start time is not initialized.");
        return Duration.ofMillis(end.getTime() - startTime.getTime());
    }

    private Duration currentSimulatedTime() {
        return sinceStart(new Date(System.currentTimeMillis()));
    }

    private Duration waitDuration(Date timestamp) {
        final Duration tillTimestamp = sinceStart(timestamp);
        final Duration nowDuration = currentSimulatedTime();

        return tillTimestamp.minus(nowDuration);
    }

    private long simulatedDurationInMillis(Duration actualDuration) {
        return actualDuration.toMillis() / servingSpeed;
    }

    private long waitTimeTill(Date timestamp) {
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
