package com.adomni.xenon;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.RawMessage;
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;
import com.erigir.wrench.QuietObjectMapper;
import lombok.Builder;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Callable;

/**
 * Given a XenonConfiguration, runs all the reports and emails the results
 *
 * Created by cweiss on 7/1/17.
 */
@Builder
public class JasperReportProcessor implements ReportProcessor{
  private static final Logger LOG = LoggerFactory.getLogger(JasperReportProcessor.class);

  public Map<Format,File> processReport(Map<String,Object> params, XenonReportDefinition report, Connection conn, XenonConfiguration global, InputStream reportDefStream)
  {
    Map<Format, File> attachmentFiles = new TreeMap<>();
    LOG.info("Processing {}",report.getConfigurationResource());

    try {
      JasperReport jr = JasperCompileManager.compileReport(reportDefStream);
      JasperPrint jp = JasperFillManager.fillReport(jr, params, conn);


      for (Format f : report.getAttachmentFormats()) {
        try {
          //File temp = File.createTempFile("XENON_", "." + f);
          //temp.deleteOnExit();
          File temp = new File("XENON."+f);

          FileOutputStream fos = new FileOutputStream(temp);
          f.processToStream(jp, fos);
          fos.flush();
          attachmentFiles.put(f, temp);
          LOG.info("Created {} as {}", f, temp);
        }
        catch (Exception e)
        {
          LOG.warn("Error processing format {}", f, e);
        }
      }
    }
    catch (Exception e)
    {
      LOG.warn("There was an issue processing report {}",report.getConfigurationResource(), e);
    }

    return attachmentFiles;
  }

  public boolean isProcessorFor(ReportType type)
  {
    return type==ReportType.JASPER_REPORT;
  }



}
