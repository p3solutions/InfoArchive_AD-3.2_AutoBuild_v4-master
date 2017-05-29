/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.server.operation;

import java.util.Map;

import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.library.basic.AbstractSingleStoreOperation;
import com.emc.documentum.xml.dds.persistence.Store;

public class RetrieveScheduledTaskProperties extends
AbstractSingleStoreOperation<Map<String, String>> {

  private String applicationName;
  private String scheduledTaskName;

  public RetrieveScheduledTaskProperties(Store store, String applicationName,
      String scheduledTaskName) throws OperationException {
    super.declareStore(store);
    this.applicationName = applicationName;
    this.scheduledTaskName = scheduledTaskName;
  }

  @Override
  public String getExecutableClassName() {
    return RetrieveScheduledTaskPropertiesExecutable.class.getName();
  }

  @Override
  public boolean isReadOnly(String storeAlias) {
    return true;
  }

  public String getApplicationName() {
    return applicationName;
  }

  public String getScheduledTaskName() {
    return scheduledTaskName;
  }
}
