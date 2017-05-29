/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client.event;

import com.emc.dds.xmlarchiving.client.configuration.NodeSetting;

public class NodeSelectedEvent implements ApplicationEvent {

  private final NodeSetting oldSetting;
  private final NodeSetting newSetting;

  public NodeSelectedEvent(NodeSetting oldSetting, NodeSetting newSetting) {
    this.oldSetting = oldSetting;
    this.newSetting = newSetting;
  }

  @Override
  public int getType() {
    return NODE_SELECTED_EVENT;
  }

  public NodeSetting getOldSetting() {
    return oldSetting;
  }

  public NodeSetting getNewSetting() {
    return newSetting;
  }

}
