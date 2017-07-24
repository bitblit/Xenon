package com.adomni.xenon.ext.cradlepoint;

import lombok.Data;

import java.net.URL;

@Data
public class CradlePointMeta {
  private int limit;
  private URL next;
  private int offset;
  private URL previous;
}
