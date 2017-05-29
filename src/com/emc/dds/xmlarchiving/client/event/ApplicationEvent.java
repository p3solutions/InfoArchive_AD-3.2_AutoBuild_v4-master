/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client.event;

/**
 * All communication between panes is handled by <code>ApplicationEvent</code> classes. WARNING:
 * This class is experimental and may change in future DDS releases.
 */
public interface ApplicationEvent {

  int SEARCH_SUBMIT_EVENT = 0;
  int SEARCH_SUBMIT_COMPLETED_EVENT = 1;
  int TREE_NODE_SELECTED_EVENT = 2;
  int SEARCH_RESULT_ITEM_SELECTED_EVENT = 3;
  int LANGUAGE_CHANGED_EVENT = 4;
  int PANE_LOADED = 6;
  int PANE_OPEN_EVENT = 7;
  int PANE_CLOSE_EVENT = 8;
  int RESIZE_EVENT = 9;
  int DIALOG_CLOSE_EVENT = 10;
  int NODE_SELECTED_EVENT = 11;
  int SEARCH_SELECTED_EVENT = 12;
  int NESTED_SEARCH_SUBMIT_EVENT = 13;

  /**
   * Returns the type of the event.
   * @return the type of the event
   */
  int getType();
}
