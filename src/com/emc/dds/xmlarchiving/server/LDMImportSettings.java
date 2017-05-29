/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.server;

import org.w3c.dom.DOMConfiguration;

import com.emc.documentum.xml.dds.util.internal.ImportSettings;

public class LDMImportSettings extends ImportSettings {

  private String retentionLibName;

  public LDMImportSettings(DOMConfiguration domConfiguration) {
    super(domConfiguration);
  }

  /**
   * Return the retention date of the item to import.
   * @return the retention date of the item to import
   */
  @Override
  public String getRetentionLibName() {
    return retentionLibName;
  }

  /**
   * Set the retention date the item to import.
   * @param retention date of the item to import
   */

  @Override
  public void setRetentionLibName(String retentionLibName) {
    this.retentionLibName = retentionLibName;
  }
}
