package com.adomni.xenon;

/**
 * Created by cweiss on 7/1/17.
 */
public enum ReportType {
  JASPER_REPORT("Processed via JasperReports", Format.values()),
  CANVAS_JS_CHART("Processed into html and canvasjs", new Format[]{Format.HTML});

  private String description;
  private Format[] availableFormats;

  ReportType(String description, Format[] availableFormats)
  {
    this.description = description; this.availableFormats = availableFormats;
  }

}
