/***************************************************************************************************
 * Copyright (c) 2009 EMC Corporation All Rights Reserved
 **************************************************************************************************/
package com.emc.dds.xmlarchiving.client.data;

import java.util.Map;

import com.emc.documentum.xml.dds.gwt.client.AbstractDDSXQueryDataSource;

public abstract class AbstractSettingDataSource extends AbstractDDSXQueryDataSource {

  private Map<String, String> fields;

  public void setFields(Map<String, String> fields) {
    this.fields = fields;
  }

  public Map<String, String> getFields() {
    return this.fields;
  }
}
