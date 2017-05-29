/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client.ui;

import com.emc.dds.xmlarchiving.client.ui.image.MainImageBundle;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Header for the application. WARNING: This class is experimental and may change in future DDS
 * releases.
 */
public class HeaderPane extends Pane {

  public HeaderPane() {
    VerticalPanel headerPanel = new VerticalPanel();
    Image image = MainImageBundle.INSTANCE.infoarchiveSmall().createImage();
    headerPanel.add(image);
    headerPanel.addStyleName(getPaneStyle());
    initWidget(headerPanel);
  }

  @Override
  public String getPaneName() {
    return HEADER_PANE_NAME;
  }

  @Override
  public int getPaneType() {
    return HEADER_PANE;
  }
}
