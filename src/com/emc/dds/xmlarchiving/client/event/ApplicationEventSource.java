/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client.event;

/**
 * Source of <code>ApplicationEvent</code> events. WARNING: This class is experimental and may
 * change in future DDS releases.
 */
public interface ApplicationEventSource {

  /**
   * Adds a listener.
   * @param listener to add
   */
  void addListener(ApplicationEventListener listener);

  /**
   * Removes a listener.
   * @param listener to remove
   */
  void removeListener(ApplicationEventListener listener);

  /**
   * Removes all listeners.
   */
  void clearListeners();

}
