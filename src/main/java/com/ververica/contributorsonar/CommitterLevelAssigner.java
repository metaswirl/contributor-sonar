package com.ververica.contributorsonar;

public class CommitterLevelAssigner {
  static final Integer BRONZE_LEVEL = 5;
  static final Integer SILVER_LEVEL = 10;
  static final Integer GOLD_LEVEL = 20;
  static final Integer PLATINUM_LEVEL = 50;

  public static CommitterLevel assignLevel(Integer numCommits) throws Exception {
    if (numCommits >= PLATINUM_LEVEL) {
      return CommitterLevel.PLATINUM;
    }
    if (numCommits >= GOLD_LEVEL) {
      return CommitterLevel.GOLD;
    }
    if (numCommits >= SILVER_LEVEL) {
      return CommitterLevel.SILVER;
    }
    if (numCommits >= BRONZE_LEVEL) {
      return CommitterLevel.BRONZE;
    }
    if (numCommits >= 1) {
      return CommitterLevel.FIRST;
    }
    throw new Exception(String.format("Illegal value %d", numCommits));
  }
}
