/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client.event;

/**
 * Event fired when a pane is closed. WARNING: This class is experimental and may change in future
 * DDS releases.
 */
public class PaneCloseEvent implements ApplicationEvent {

  private int paneType;

  /**
   * Constructs a pane close event.
   * @param paneType the type of the pane that is closed
   */
  public PaneCloseEvent(int paneType) {
    super();
    this.paneType = paneType;
  }

  /**
   * Returns the type of the event.
   * @return the type of the event
   */
  @Override
  public int getType() {
    return PANE_CLOSE_EVENT;
  }

  /**
   * Returns the type of the pane that is closed.
   * @return the type of the pane that is closed
   */
  public int getPaneType() {
    return paneType;
  }

}
