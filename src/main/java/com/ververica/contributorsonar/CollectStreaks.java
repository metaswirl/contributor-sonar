package com.ververica.contributorsonar;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

class CollectStreaks extends RichFlatMapFunction<Commit, AwardStreak> {

  private transient ValueState<Tuple3<Integer, Integer, Integer>> streak;

  @Override
  public void flatMap(Commit commit, Collector<AwardStreak> collector) throws Exception {
    final int weekOfYear = TimeUtils.extractWeekInYear(commit.getEventTime());
    final int year = TimeUtils.extractYear(commit.getEventTime());

    final Tuple3<Integer, Integer, Integer> lastState = streak.value();
    if (isInWeek(lastState.f0, lastState.f1, commit.getEventTime())) {
      // do nothing
    } else if (isInPreviousWeek(lastState.f0, lastState.f1, commit.getEventTime())) {
      int newStreak = lastState.f2 + 1;
      streak.update(new Tuple3<>(year, weekOfYear, newStreak));
      collector.collect(new AwardStreak(commit.authorName, commit.committedTimestamp, newStreak));
    } else {
      streak.update(new Tuple3<>(year, weekOfYear, 1));
    }
  }

  private boolean isInPreviousWeek(int currentStateYear, int currentStateWeek, Instant timestamp) {
    return isInWeek(currentStateYear, currentStateWeek, timestamp.minus(7, ChronoUnit.DAYS));
  }

  private boolean isInWeek(int currentStateYear, int currentStateWeek, Instant timestamp) {
    return TimeUtils.extractYear(timestamp) == currentStateYear
        && TimeUtils.extractWeekInYear(timestamp) == currentStateWeek;
  }

  @Override
  public void open(Configuration config) {
    ValueStateDescriptor<Tuple3<Integer, Integer, Integer>> descriptor =
        new ValueStateDescriptor<>(
            "weekly-streak", // the state name
            TypeInformation.of(
                new TypeHint<Tuple3<Integer, Integer, Integer>>() {}), // type information
            Tuple3.of(0, 0, 0)); // default value of the state, if nothing was set
    streak = getRuntimeContext().getState(descriptor);
  }
}
