package com.adomni.xenon;

/**
 * Created by cweiss on 7/1/17.
 */
public enum RunResult {
  SUCCESS("The report ran successfully"), ERROR("The report failed to run correctly"), SKIPPED("The report is marked skipped for this run"),
  PARTIAL_SUCCESS("Some reports ran ok, but some formats failed");

  private String description;

  RunResult(String description)
  {
    this.description = description;
  }

}
