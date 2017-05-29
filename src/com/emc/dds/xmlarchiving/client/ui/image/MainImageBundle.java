/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client.ui.image;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * Icon image bundle.
 */
public interface MainImageBundle extends
    com.emc.documentum.xml.dds.gwt.client.ui.image.IconImageBundle {

  static final MainImageBundle INSTANCE = GWT.create(MainImageBundle.class);

  AbstractImagePrototype company();

  AbstractImagePrototype companySmall();

  AbstractImagePrototype infoarchive();

  AbstractImagePrototype infoarchiveSmall();

  AbstractImagePrototype allapplications16();

  AbstractImagePrototype businessobject16();

  /**
   * Gets the icon for the check in a selected {@link MenuItem} (16x16 pixels).
   * @return the icon for the check in a selected {@link MenuItem} (16x16 pixels)
   */
  AbstractImagePrototype check16();

  /**
   * Gets the empty icon (16x16 pixels).
   * @return the empty icon (16x16 pixels)
   */
  AbstractImagePrototype empty16();

  /**
   * Gets the application icon (16x16 pixels).
   * @return the application icon (16x16 pixels)
   */
  AbstractImagePrototype application16();

  AbstractImagePrototype hold22();

  AbstractImagePrototype pdf22();

}
