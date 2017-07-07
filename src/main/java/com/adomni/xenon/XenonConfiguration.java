package com.adomni.xenon;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by cweiss on 7/1/17.
 */
@Data
public class XenonConfiguration {
  private String fromEmail;
  private String adminEmail;
  private List<XenonReportDefinition> reports;
  private Map<String,List<String>> recipientLists;
  private TunneledMysqlConfig sshTunnel;
  private Map<String,Object> globalReportParams;
}
