package com.adomni.xenon;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Map;

public interface ReportProcessor {
  boolean isProcessorFor(ReportType type);
  /* For each format, output a temp file with that content */
  Map<Format, File> processReport(Map<String,Object> allParams, XenonReportDefinition report, Connection conn, XenonConfiguration global, InputStream reportDefStream);
}
