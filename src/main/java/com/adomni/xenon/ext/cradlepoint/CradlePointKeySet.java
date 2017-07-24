package com.adomni.xenon.ext.cradlepoint;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

@Builder
@Data
public class CradlePointKeySet {
  private String ecmApiId;
  private String ecmApiKey;
  private String cpApiId;
  private String cpApiKey;


  public static CradlePointKeySet fromEnvironment()
  {
    CradlePointKeySet rval = CradlePointKeySet.builder()
        .ecmApiId(StringUtils.trimToNull(System.getenv("CRADLEPOINT_ECM_API_ID")))
        .ecmApiKey(StringUtils.trimToNull(System.getenv("CRADLEPOINT_ECM_API_KEY")))
        .cpApiId(StringUtils.trimToNull(System.getenv("CRADLEPOINT_CP_API_ID")))
        .cpApiKey(StringUtils.trimToNull(System.getenv("CRADLEPOINT_CP_API_KEY")))
        .build();
    return rval;
  }


}
