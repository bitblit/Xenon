package com.adomni.xenon.ext.cradlepoint;

import lombok.Data;

import java.util.List;

@Data
public class RoutersResponse {
  private List<RouterData> data;
  private CradlePointMeta meta;
}
