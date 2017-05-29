/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.server.scheduling;

public class LDMScheduledHandleIncoming extends LDMScheduledTask {

  String xmlextensions;
  String sharedlocation;
  int maxThreads;

  public LDMScheduledHandleIncoming(String interval, boolean activated, String type, String name,
      String xmlextensions, String sharedlocation, int maxThreads) {
    super(interval, activated, type, name);
    this.xmlextensions = xmlextensions;
    this.sharedlocation = sharedlocation;
    this.maxThreads = maxThreads;
  }

  public String getXmlextensions() {
    return xmlextensions;
  }

  public String getSharedlocation() {
    return sharedlocation;
  }

  public int getMaxThreads() {
    return maxThreads;
  }
}
