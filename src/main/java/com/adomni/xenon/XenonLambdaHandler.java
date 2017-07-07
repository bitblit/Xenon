package com.adomni.xenon;


import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.erigir.wrench.QuietObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by cweiss on 6/28/17.
 */
public class XenonLambdaHandler implements RequestHandler<XenonLambdaRequest, XenonLambdaResponse> {
  private static final Logger LOG = LoggerFactory.getLogger(XenonLambdaHandler.class);

  private QuietObjectMapper objectMapper = new QuietObjectMapper();
  private ExecutorService executorService = Executors.newFixedThreadPool(5);


  @Override
  public XenonLambdaResponse handleRequest(XenonLambdaRequest xenonLambdaRequest, Context context) {
    XenonLambdaResponse resp = new XenonLambdaResponse();
    try {
      DefaultAWSCredentialsProviderChain awsCredentials = new DefaultAWSCredentialsProviderChain();
      AmazonSimpleEmailService ses = AmazonSimpleEmailServiceClient.builder().withCredentials(awsCredentials).build();
      AmazonS3 s3 = AmazonS3Client.builder().withCredentials(awsCredentials).build();

      String sourceLocation = StringUtils.trimToNull(System.getenv("XENON_ROOT"));
      if (sourceLocation == null) {
        resp.setError("No source location found");
      } else {
        resp.setSource(sourceLocation);
        XenonReportRunner xrr = XenonReportRunner.builder()
            .objectMapper(objectMapper)
            .xenonRoot(sourceLocation)
            .ses(ses)
            .s3(s3)
            .build();
        Future<Map<String,RunResult>> toWait = executorService.submit(xrr);

        Map<String,RunResult> rval = toWait.get(280, TimeUnit.SECONDS); // to have time to cleanup
        resp.setResults(rval);
      }
    }
    catch (Exception e)
    {
      resp.setError("Failed to perform report running due to : "+ ExceptionUtils.getStackTrace(e));
    }

    return resp;
  }

}
