/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client.ui;

import com.emc.dds.xmlarchiving.client.i18n.Locale;
import com.emc.documentum.xml.dds.gwt.client.DDSConstants;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Footer for the application. WARNING: This class is experimental and may change in future DDS
 * releases.
 */
public class FooterPane extends Pane {

  /**
   * Creates a new instance.
   */
  public FooterPane() {
    HorizontalPanel footerPanel = new HorizontalPanel();
    footerPanel.addStyleName(getPaneStyle());
    initWidget(footerPanel);

    String copyrightEMC = Locale.getLabels().copyrightEMC();
    String productName = Locale.getLabels().dds();
    String productVersion = DDSConstants.DDS_VERSION;
    Label versionLabel = new Label(productName + " " + productVersion + " - " + copyrightEMC);
    footerPanel.add(versionLabel);
    footerPanel.setCellHorizontalAlignment(versionLabel, HasHorizontalAlignment.ALIGN_LEFT);
  }

  @Override
  public String getPaneName() {
    return FOOTER_PANE_NAME;
  }

  @Override
  public int getPaneType() {
    return FOOTER_PANE;
  }
}
