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
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
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
public class XenonReportRunner implements Callable<Map<String, RunResult>>{
  private static final Logger LOG = LoggerFactory.getLogger(XenonReportRunner.class);
  private static final String XENON_CONFIG_FILENAME="xenon_config.json";

  private String xenonRoot;
  private AmazonSimpleEmailService ses;
  private AmazonS3 s3;
  private QuietObjectMapper objectMapper;

  public Map<String, RunResult> call()
    throws Exception
  {
    Map<String,RunResult> rval = new TreeMap<>();

    XenonConfiguration config = loadConfiguration();
    LOG.info("Opening tunneled connection");
    TunneledMariaDB tmd = new TunneledMariaDB(config.getSshTunnel());
    Connection conn = tmd.getConnection();

    LOG.info("Processing {} reports",config.getReports().size());

    for (XenonReportDefinition rep:config.getReports())
    {
      RunResult rr = processSingleReport(rep,conn,config);
      rval.put(rep.getConfigurationResource(),rr);
    }

    LOG.info("Shutting down db connection");
    tmd.shutdown();

    return rval;
  }

  private InputStream fetchFile(String path)
  {
    Objects.requireNonNull(xenonRoot);
    Objects.requireNonNull(path);
    try {
      if (xenonRoot.startsWith("s3://")) {
        LOG.debug("Processing reads from S3 : {}", path);
        AmazonS3URI aUri = new AmazonS3URI(xenonRoot);

        String newPath = (aUri.getKey()==null)?path:aUri.getKey() + "/" + path;

        S3Object sob = s3.getObject(aUri.getBucket(), newPath);
        return sob.getObjectContent();
      } else {
        LOG.debug("Processing default read: {}", path);
        String pathToConfig = xenonRoot + "/" + XENON_CONFIG_FILENAME;
        URL url = new URL(pathToConfig);
        return url.openStream();
      }
    }
    catch (Exception e)
    {
      throw new RuntimeException("Failed to open stream",e);
    }
  }


  private XenonConfiguration loadConfiguration()
  {
      XenonConfiguration rval = objectMapper.readValue(fetchFile(XENON_CONFIG_FILENAME), XenonConfiguration.class);
      return rval;
  }

  private RunResult processSingleReport(XenonReportDefinition report, Connection conn, XenonConfiguration global)
  {
    RunResult rval = RunResult.SUCCESS;

    LOG.info("Processing {}",report.getConfigurationResource());

    try {
      Map<String, Object> params = new TreeMap<>();
      params.putAll(global.getGlobalReportParams());
      params.putAll(report.getReportParams());

      JasperReport jr = JasperCompileManager.compileReport(fetchFile(report.getConfigurationResource()));
      JasperPrint jp = JasperFillManager.fillReport(jr, params, conn);

      LOG.info("Report created - creating body text");
      String body = StringUtils.trimToEmpty(report.getBodyText());
      body += (report.getBodyFormat() == null || !report.getBodyFormat().isUsableAsEmailBody()) ? "" : new String(report.getBodyFormat().processToByteArray(jp));

      Map<Format, File> attachmentFiles = new TreeMap<>();
      for (Format f : report.getAttachmentFormats()) {
        try {
          File temp = File.createTempFile("XENON_", "." + f);
          temp.deleteOnExit();
          FileOutputStream fos = new FileOutputStream(temp);
          f.processToStream(jp, fos);
          fos.flush();
          attachmentFiles.put(f, temp);
          LOG.info("Created {} as {}", f, temp);
        }
        catch (Exception e)
        {
          LOG.warn("Error processing format {}", f, e);
          rval = RunResult.PARTIAL_SUCCESS;
        }
      }

      LOG.info("All versions created, creating email");

      Session s = Session.getInstance(new Properties(), null);
      MimeMessage mimeMessage = new MimeMessage(s);

      Set<String> allRecipients = new TreeSet<>();
      // Sender and recipient
      mimeMessage.setFrom(new InternetAddress(global.getFromEmail()));
      for (String list:report.getRecipientLists())
      {
        List<String> targets =  global.getRecipientLists().get(list);
        if (targets==null)
        {
          LOG.warn("Could not find list {}",list);
        }
        else
        {
          allRecipients.addAll(targets);
        }
      }

      for (String toMail:allRecipients)
      {
        mimeMessage.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(toMail));
      }

      // Subject
      mimeMessage.setSubject(report.getSubject());

      // Add a MIME part to the message
      MimeMultipart mimeBodyPart = new MimeMultipart();
      BodyPart part = new MimeBodyPart();
      part.setContent(body,"text/html; charset=UTF-8");
      mimeBodyPart.addBodyPart(part);

      for (Map.Entry<Format,File> e:attachmentFiles.entrySet())
      {
        // Add a attachement to the message
        part = new MimeBodyPart();
        DataSource source = new FileDataSource(e.getValue());
        part.setDataHandler(new DataHandler(source));
        part.setFileName("REPORT."+e.getKey());
        mimeBodyPart.addBodyPart(part);
      }

      mimeMessage.setContent(mimeBodyPart);

      // Create Raw message
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      mimeMessage.writeTo(outputStream);
      RawMessage rawMessage = new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));

      // Send Mail
      SendRawEmailRequest rawEmailRequest = new SendRawEmailRequest(rawMessage);
      rawEmailRequest.setDestinations(allRecipients);
      rawEmailRequest.setSource(global.getFromEmail());
      ses.sendRawEmail(rawEmailRequest);
    }
    catch (Exception e)
    {
      LOG.warn("There was an issue processing report {}",report.getConfigurationResource(), e);
      rval = RunResult.ERROR;
    }

    return rval;
  }


}
