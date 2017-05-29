/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.server.operation;

import java.util.Map;

import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.library.basic.AbstractSingleStoreOperation;
import com.emc.documentum.xml.dds.persistence.Store;

public class RetrieveIncomingScheduledTaskProperties extends
AbstractSingleStoreOperation<Map<String, String>> {

  private String applicationName;

  public RetrieveIncomingScheduledTaskProperties(Store store, String applicationName)
      throws OperationException {
    super.declareStore(store);
    this.applicationName = applicationName;
  }

  @Override
  public String getExecutableClassName() {
    return RetrieveIncomingScheduledTaskPropertiesExecutable.class.getName();
  }

  @Override
  public boolean isReadOnly(String storeAlias) {
    return true;
  }

  public String getApplicationName() {
    return applicationName;
  }
}
