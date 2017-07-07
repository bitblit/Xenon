package com.adomni.xenon;

import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by cweiss on 7/1/17.
 */
@Data
public class XenonReportDefinition {
  private String configurationResource;
  private Format bodyFormat;
  private Set<Format> attachmentFormats;
  private String subject;
  private String bodyText;
  private Map<String,Object> reportParams;
  private List<String> recipientLists;
}
