package com.adomni.xenon.ext.cradlepoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.net.URL;
import java.time.ZonedDateTime;

@Data
public class RouterData {
  private URL account;
  @JsonProperty("actual_firmware")
  private URL actualFirmware;
  @JsonProperty("asset_id")
  private String assetId;
  @JsonProperty("config_status")
  private String configStatus;
  @JsonProperty("created_at")
  private ZonedDateTime createdAt;
  private String custom1;
  private String custom2;
  private String description;
  @JsonProperty("full_product_name")
  private String fullProductName;
  private URL group;
  private String id;
  @JsonProperty("ipv4_address")
  private String ipv4Address;
  private String locality;
  @JsonProperty("mac")
  private String macAddress;
  private String name;
  private URL product;

}
