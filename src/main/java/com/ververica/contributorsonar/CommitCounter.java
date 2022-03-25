package com.ververica.contributorsonar;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;

public class CommitCounter extends RichFlatMapFunction<Commit, AwardCommit> {
  ValueState<Integer> count;
  ValueState<CommitterLevel> level;

  @Override
  public void open(Configuration parameters) throws Exception {
    super.open(parameters);
    ValueStateDescriptor<Integer> descCount =
        new ValueStateDescriptor<>("commitCount", Integer.class);
    ValueStateDescriptor<CommitterLevel> descLevel =
        new ValueStateDescriptor<>("commitLevel", CommitterLevel.class);
    count = getRuntimeContext().getState(descCount);
    level = getRuntimeContext().getState(descLevel);
  }

  @Override
  public void flatMap(Commit commit, Collector<AwardCommit> collector) throws Exception {
    Integer currentCount = count.value() == null ? 1 : count.value() + 1;
    count.update(currentCount);
    CommitterLevel newLevel = CommitterLevelAssigner.assignLevel(currentCount);
    CommitterLevel oldLevel = level.value() == null ? CommitterLevel.NONE : level.value();
    if (newLevel != oldLevel) {
      collector.collect(new AwardCommit(newLevel, commit.authorName, commit.committedTimestamp));
      level.update(newLevel);
    }
  }
}
