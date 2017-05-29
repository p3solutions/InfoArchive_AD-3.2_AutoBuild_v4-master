/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client.event;

/**
 * Event fired when a tree node is selected. WARNING: This class is experimental and may change in
 * future DDS releases.
 */
public class TreeNodeSelectedEvent implements ApplicationEvent {

  private String uri;

  /**
   * Constructs a tree node selected event.
   * @param uri the DDS uri of the item selected.
   */
  public TreeNodeSelectedEvent(String uri) {
    super();
    this.uri = uri;
  }

  /**
   * Returns the type of the event.
   * @returns the type of the event.
   */
  @Override
  public int getType() {
    return TREE_NODE_SELECTED_EVENT;
  }

  /**
   * Returns the DDS uri of the selected item.
   * @return the DDS uri of the selected item.
   */
  public String getUri() {
    return uri;
  }
}
