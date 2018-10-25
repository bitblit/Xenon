package com.adomni.xenon;

import lombok.Builder;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

/**
 * Given a XenonConfiguration, runs all the reports and emails the results
 *
 * Created by cweiss on 7/1/17.
 */
@Builder
public class CanvasJSReportProcessor implements ReportProcessor{
  private static final Logger LOG = LoggerFactory.getLogger(CanvasJSReportProcessor.class);

  public Map<Format,File> processReport(Map<String,Object> allParams, XenonReportDefinition report, Connection conn, XenonConfiguration global, InputStream reportDefStream)
  {
    Map<Format, File> attachmentFiles = new TreeMap<>();

    LOG.info("Processing {}",report.getConfigurationResource());

    try {
      String type = (String)allParams.get("type");

      if ("stacked-bar".equals(type))
      {
        String html = generateStackedBar(allParams, conn);
        Format f = Format.HTML;
        File temp = new File("XENON."+f);
        FileUtils.writeStringToFile(temp, html, Charset.defaultCharset());
        attachmentFiles.put(f, temp);
        LOG.info("Created {} as {}", f, temp);

      }
      else
      {
        LOG.warn("Unrecognized type : {}",type);
      }
    }
    catch (Exception e)
    {
      LOG.warn("There was an issue processing report {}",report.getConfigurationResource(), e);
    }

    return attachmentFiles;
  }

  private String generateStackedBar(Map<String,Object> allParams, Connection conn)
      throws IOException, SQLException
  {
    String template = IOUtils.toString(getClass().getResourceAsStream("/CanvasJSStackedBarTemplate.html"), Charset.defaultCharset());
    String sql = (String)allParams.get("sql");
    PreparedStatement ps = conn.prepareStatement(sql);
    ResultSet rs = ps.executeQuery();

    // TODO: Finish implementing
    return null;
  }


  public boolean isProcessorFor(ReportType type)
  {
    return type==ReportType.CANVAS_JS_CHART;
  }


}
