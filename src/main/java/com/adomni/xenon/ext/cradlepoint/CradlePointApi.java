package com.adomni.xenon.ext.cradlepoint;

import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedList;
import java.util.List;

/**
 * See : https://developer.cradlepoint.com/#!/API_Examples/routers
 */
@Builder
public class CradlePointApi {
  private static final Logger LOG = LoggerFactory.getLogger(CradlePointApi.class);

  @Builder.Default
  private String apiPrefix =  "https://www.cradlepointecm.com/api/v2/";
  @Builder.Default
  private String routerSuffix = "routers/";

  private RestTemplate restTemplate;

  public List<RouterData> fetchAllRouters()
  {
    List<RouterData> rval = new LinkedList<>();

    String url = apiPrefix+routerSuffix;

    while (url!=null) {
      LOG.debug("Fetching : {}",url);
      RoutersResponse rr = restTemplate.getForObject(url, RoutersResponse.class);
      rval.addAll(rr.getData());
      url = (rr.getMeta() == null || rr.getMeta().getNext() == null) ? null : rr.getMeta().getNext().toString();
    }

    return rval;
  }


}
