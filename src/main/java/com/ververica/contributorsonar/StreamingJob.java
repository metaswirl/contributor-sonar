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

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

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

  public static void main(String[] args) throws Exception {
    final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    String filePath = System.getProperty("user.dir") + "/data/last_commits.txt";
    DataStream<Commit> stream =
        env.addSource(new FileSource<>(filePath, new CommitDeserializer(), 24 * 60 * 60 * 10))
            .returns(Commit.class);

    // createCollectStreaksJob(stream);
    createCommitCounterJob(stream);
    env.execute();
  }

  public static void createCommitCounterJob(DataStream<Commit> stream) {
    stream
        .filter(x -> x.authorId != null && x.authorName != null)
        .keyBy(x -> x.authorId)
        .flatMap(new CommitCounter())
        .print();
  }

  public static void createCollectStreaksJob(DataStream<Commit> stream) {
    stream
        .filter(x -> x.authorId != null && x.authorName != null)
        .keyBy(x -> x.authorId)
        .flatMap(new CollectStreaks())
        .print();
  }
}
