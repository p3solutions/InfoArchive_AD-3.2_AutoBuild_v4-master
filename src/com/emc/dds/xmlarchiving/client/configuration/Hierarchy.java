/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client.configuration;

public class Hierarchy {

  private String title;

  private NodeSetting rootNodeSetting;

  public Hierarchy(String title, NodeSetting rootNodeSetting) {
    super();
    this.title = title;
    this.rootNodeSetting = rootNodeSetting;
  }

  public String getTitle() {
    return title;
  }

  public NodeSetting getRootNodeSetting() {
    return rootNodeSetting;
  }
}
