package com.adomni.xenon;

import com.amazonaws.services.lambda.runtime.Context;
import org.easymock.EasyMockRule;
import org.easymock.Mock;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

/**
 * Created by cweiss on 7/5/17.
 */
public class TestXenonLambdaHandler {
  private static final Logger LOG = LoggerFactory.getLogger(TestXenonLambdaHandler.class);

  @Rule
  public EasyMockRule rule = new EasyMockRule(this);

  @Mock
  private Context ctx;

  @Test
  public void testHandler()
      throws Exception
  {
    XenonLambdaHandler inst = new XenonLambdaHandler();
    XenonLambdaRequest req = new XenonLambdaRequest();
    XenonLambdaResponse resp = inst.handleRequest(req,ctx);

    assertNotNull(resp);

    if (resp.getError()!=null)
    {
      LOG.warn("Error was {}",resp.getError());
    }

  }
}
