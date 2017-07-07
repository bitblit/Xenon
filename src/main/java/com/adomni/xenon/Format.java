package com.adomni.xenon;

import com.lowagie.text.pdf.PdfWriter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterContext;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by cweiss on 7/1/17.
 */
public enum Format {
  CSV(true), HTML(true), XLS(false), PDF(false);

  private boolean usableAsEmailBody;

  Format(boolean usableAsEmailBody)
  {
    this.usableAsEmailBody = usableAsEmailBody;
  }

  public boolean isUsableAsEmailBody() {
    return usableAsEmailBody;
  }

  public byte[] processToByteArray(JasperPrint jp)
  {
    ByteArrayOutputStream boas = new ByteArrayOutputStream();
    processToStream(jp,boas);
    return boas.toByteArray();
  }

  public void processToStream(JasperPrint jp, OutputStream os)
  {
    try
    {
      switch(this) {
        case CSV:
          JRCsvExporter exCSV = new JRCsvExporter();
          exCSV.setExporterInput(new SimpleExporterInput(jp));
          exCSV.setExporterOutput(new SimpleWriterExporterOutput(os));
          exCSV.exportReport();
          break;
        case HTML:
          HtmlExporter exHTML = new HtmlExporter();
          exHTML.setExporterInput(new SimpleExporterInput(jp));
          exHTML.setExporterOutput(new SimpleHtmlExporterOutput(os));
          exHTML.exportReport();
          break;
        case XLS:
          JRXlsExporter exXLS = new JRXlsExporter();
          exXLS.setExporterInput(new SimpleExporterInput(jp));
          exXLS.setExporterOutput(new SimpleOutputStreamExporterOutput(os));
          exXLS.exportReport();
          break;
        case PDF:
          JRPdfExporter exPDF = new JRPdfExporter();
          exPDF.setExporterInput(new SimpleExporterInput(jp));
          exPDF.setExporterOutput(new SimpleOutputStreamExporterOutput(os));
          SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
          configuration.setPermissions(PdfWriter.ALLOW_COPY | PdfWriter.ALLOW_PRINTING);
          exPDF.setConfiguration(configuration);
          exPDF.exportReport();
          break;
        default:
          throw new RuntimeException("Cant happen - invalid type : " + this);
      }
    }
    catch (JRException jre)
    {
      throw new RuntimeException("Error processing jasper report",jre);
    }
  }

}
