package com.adomni.xenon.ext.cradlepoint;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

public class TestCradlePointApi {
  private static final Logger LOG = LoggerFactory.getLogger(TestCradlePointApi.class);

  @Test
  @Ignore
  public void testFetchAllRouters()
      throws Exception
  {
    ApplicationContext ctx = new AnnotationConfigApplicationContext(CradlePointSpringConfig.class);
    CradlePointApi api = ctx.getBean("cradlePointApi", CradlePointApi.class);

    List<RouterData> routerData = api.fetchAllRouters();
    LOG.info("Found {}",routerData);

  }
}
