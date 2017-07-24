package com.adomni.xenon.ext.cradlepoint;

import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

@Builder
public class SetAuthHeadersInterceptor  implements ClientHttpRequestInterceptor {
  private static final Logger LOG = LoggerFactory.getLogger(SetAuthHeadersInterceptor.class);
  private CradlePointKeySet cradlePointKeySet;

  @Override
  public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
    HttpHeaders headers = httpRequest.getHeaders();

    headers.add("X-ECM-API-ID",cradlePointKeySet.getEcmApiId());
    headers.add("X-ECM-API-KEY",cradlePointKeySet.getEcmApiKey());
    headers.add("X-CP-API-ID",cradlePointKeySet.getCpApiId());
    headers.add("X-CP-API-KEY",cradlePointKeySet.getCpApiKey());

    return clientHttpRequestExecution.execute(httpRequest, bytes);
  }
}
