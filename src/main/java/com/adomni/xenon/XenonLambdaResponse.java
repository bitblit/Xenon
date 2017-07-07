package com.adomni.xenon;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by cweiss on 6/28/17.
 */
@Data
public class XenonLambdaResponse {
  private static final Logger LOG = LoggerFactory.getLogger(XenonLambdaResponse.class);
  private String source;
  private String error;
  private Map<String,RunResult> results;


}
