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

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

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

    String filePath = System.getProperty("user.dir") + "/data/commits.txt";
    DataStreamSource<Commit> source =
        env.addSource(new FileSource<>(filePath, new CommitDeserializer(), 24 * 60 * 60));
    source.returns(Commit.class);

    StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
    tableEnv.createTemporaryView("Commits", source);
    // Schema schema = Schema.newBuilder().column("deletionCount", DataType)

    tableEnv
        .executeSql(
            "SELECT authoredTimestamp, committedTimestamp, authorName, committerName, message FROM Commits")
        .print();
  }
}
