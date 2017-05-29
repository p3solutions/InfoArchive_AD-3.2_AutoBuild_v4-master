/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.server.operation;

import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.library.basic.AbstractSingleStoreOperation;
import com.emc.documentum.xml.dds.persistence.Store;

public class RemoveUnusedSegmentsOperation extends AbstractSingleStoreOperation<Object> {

  private String id;

  public RemoveUnusedSegmentsOperation(Store store, String id) throws OperationException {
    super.declareStore(store);
    this.id = id;
  }

  @Override
  public String getExecutableClassName() {
    return RemoveUnusedSegmentsOperationExecutable.class.getName();
  }

  @Override
  public boolean isReadOnly(String storeAlias) {
    return false;
  }

  @Override
  public String getId() {
    return id;
  }

}
