/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.server.operation;

import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.library.basic.AbstractSingleStoreOperation;
import com.emc.documentum.xml.dds.persistence.Store;

public class HandleScheduledXMLArchivingOperation extends AbstractSingleStoreOperation<Boolean> {

  private String id;
  private String operationName;
  private boolean readOnly;

  public HandleScheduledXMLArchivingOperation(Store store, String id, String operationName,
      boolean readOnly) throws OperationException {
    declareStore(store);
    this.id = id;
    this.operationName = operationName;
    this.readOnly = readOnly;
  }

  @Override
  public String getExecutableClassName() {
    return HandleScheduledXMLArchivingOperationExecutable.class.getName();
  }

  @Override
  public boolean isReadOnly(String storeAlias) {
    return readOnly;
  }

  public boolean isReadOnly() {
    return readOnly;
  }

  @Override
  public String getId() {
    return id;
  }

  public String getOperationName() {
    return operationName;
  }
}
