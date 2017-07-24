package com.adomni.xenon.ext.cradlepoint;

import org.apache.regexp.RE;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.Set;

@Configuration
public class CradlePointSpringConfig {

  @Bean
  public CradlePointKeySet cradlePointKeySet()
  {
    CradlePointKeySet bean = CradlePointKeySet.fromEnvironment();
    return bean;
  }

  @Bean
  public SetAuthHeadersInterceptor setAuthHeadersInterceptor()
  {
    SetAuthHeadersInterceptor bean = SetAuthHeadersInterceptor.builder().cradlePointKeySet(cradlePointKeySet())
        .build();
    return bean;
  }

  @Bean
  public RestTemplate cradlePointRestTemplate()
  {
    RestTemplate bean = new RestTemplate();
    bean.getInterceptors().add(setAuthHeadersInterceptor());
    return bean;
  }

  @Bean
  public CradlePointApi cradlePointApi()
  {
    CradlePointApi bean = CradlePointApi.builder()
        .restTemplate(cradlePointRestTemplate())
        .build();
    return bean;
  }



}
