/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.server.operation;

import java.util.List;

import com.emc.dds.xmlarchiving.server.scheduling.LDMScheduledTask;
import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.library.basic.AbstractSingleStoreOperation;
import com.emc.documentum.xml.dds.persistence.Store;

public class LoadScheduledTasksOperation extends
AbstractSingleStoreOperation<List<LDMScheduledTask>> {

  private String applicationName;

  public LoadScheduledTasksOperation(Store store, String applicationName) throws OperationException {
    super.declareStore(store);
    this.applicationName = applicationName;
  }

  @Override
  public String getExecutableClassName() {
    return LoadScheduledTasksOperationExecutable.class.getName();
  }

  @Override
  public boolean isReadOnly(String storeAlias) {
    return true;
  }

  public String getApplicationName() {
    return applicationName;
  }
}
