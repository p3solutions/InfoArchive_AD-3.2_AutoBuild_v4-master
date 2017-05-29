/***************************************************************************************************
 * Copyright (c) 2011 EMC Corporation All Rights Reserved
 **************************************************************************************************/
package com.emc.dds.xmlarchiving.client.event;

import java.util.Map;

public class NestedSearchSubmitEvent extends SearchSubmitEvent {

  private String nodeId;

  private String searchConfiguration;

  public NestedSearchSubmitEvent(Map<String, String> fields, String nodeId,
      String searchConfiguration) {
    super(fields);
    this.nodeId = nodeId;
    this.searchConfiguration = searchConfiguration;
  }

  @Override
  public int getType() {
    return NESTED_SEARCH_SUBMIT_EVENT;
  }

  public String getNodeId() {
    return this.nodeId;
  }

  public String getSearchConfiguration() {
    return this.searchConfiguration;
  }
}
