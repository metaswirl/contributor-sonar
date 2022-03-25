/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ververica.contributorsonar;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

import java.time.Instant;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

/**
 * Skeleton for a Flink Streaming Job.
 *
 * <p>For a tutorial how to write a Flink streaming application, check the tutorials and examples on
 * the <a href="https://flink.apache.org/docs/stable/">Flink Website</a>.
 *
 * <p>To package your application into a JAR file for execution, run 'mvn clean package' on the
 * command line.
 *
 * <p>If you change the name of the main class (with the public static void main(String[] args))
 * method, change the respective entry in the POM.xml file (simply search for 'mainClass').
 */
public class StreamingJob {

  private static class CollectStreaks extends RichFlatMapFunction<Commit, Tuple2<String, Integer>> {

    private transient ValueState<Tuple3<Integer, Integer, Integer>> streak;

    @Override
    public void flatMap(Commit commit, Collector<Tuple2<String, Integer>> collector)
        throws Exception {
      final int year = commit.getEventTime().get(ChronoField.YEAR);
      final int weekOfYear = commit.getEventTime().get(ChronoField.ALIGNED_WEEK_OF_YEAR);

      final Tuple3<Integer, Integer, Integer> lastState = streak.value();
      if (isInWeek(lastState.f0, lastState.f1, commit.getEventTime())) {
        // do nothing
      } else if (isInPreviousWeek(lastState.f0, lastState.f1, commit.getEventTime())) {
        int newStreak = lastState.f2 + 1;
        streak.update(new Tuple3<>(year, weekOfYear, newStreak));
        collector.collect(new Tuple2<>(commit.authorLogin, newStreak));
      } else {
        streak.update(new Tuple3<>(year, weekOfYear, 1));
      }
    }

    private boolean isInPreviousWeek(
        int currentStateYear, int currentStateWeek, Instant timestamp) {
      return isInWeek(currentStateYear, currentStateWeek, timestamp.minus(7, ChronoUnit.DAYS));
    }

    private boolean isInWeek(int currentStateYear, int currentStateWeek, Instant timestamp) {
      return timestamp.get(ChronoField.YEAR) == currentStateYear
          && timestamp.get(ChronoField.ALIGNED_WEEK_OF_YEAR) == currentStateWeek;
    }

    @Override
    public void open(Configuration config) {
      ValueStateDescriptor<Tuple3<Integer, Integer, Integer>> descriptor =
          new ValueStateDescriptor<>(
              "average", // the state name
              TypeInformation.of(
                  new TypeHint<Tuple3<Integer, Integer, Integer>>() {}), // type information
              Tuple3.of(0, 0, 0)); // default value of the state, if nothing was set
      streak = getRuntimeContext().getState(descriptor);
    }
  }

  public static void main(String[] args) throws Exception {
    final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    String filePath = System.getProperty("user.dir") + "/data/commits.txt";
    env.addSource(new FileSource<>(filePath, new CommitDeserializer(), 24 * 60 * 60))
        .returns(Commit.class)
        .keyBy(Commit::getEventTime)
        .flatMap(new CollectStreaks())
        .print();

    env.execute();

    //    tableEnv
    //        .executeSql(
    //            "SELECT each_year, week, authorName, COUNT(*) AS cnt FROM (SELECT extract(WEEK
    // FROM authoredTimestamp) AS week, extract(YEAR FROM authoredTimestamp) AS each_year,
    // authorName FROM Commits) GROUP BY authorName, each_year, week")
    //        .print();
  }
}
