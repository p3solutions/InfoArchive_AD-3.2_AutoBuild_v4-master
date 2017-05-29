/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client.event;

/**
 * Event fired when the a pane has finished loading data. WARNING: This class is experimental and
 * may change in future DDS releases.
 */
public class PaneLoadedEvent implements ApplicationEvent {

  private int paneType;

  /**
   * Constructs a pane loaded event.
   * @param paneType the type of the pane that has finished loading data.
   */
  public PaneLoadedEvent(int paneType) {
    super();
    this.paneType = paneType;
  }

  /**
   * Returns the type of the pane.
   * @return the type of the pane.
   */
  public int getPaneType() {
    return paneType;
  }

  /**
   * Returns the type of the event.
   * @returns the type of the event.
   */
  @Override
  public int getType() {
    return ApplicationEvent.PANE_LOADED;
  }
}
