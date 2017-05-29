/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client.event;

/**
 * Listener of <code>ApplicationEvent</code> events. WARNING: This class is experimental and may
 * change in future DDS releases.
 */
public interface ApplicationEventListener {

  /**
   * Handles the events.
   * @param event the event to handle
   */
  void handle(ApplicationEvent event);

}
