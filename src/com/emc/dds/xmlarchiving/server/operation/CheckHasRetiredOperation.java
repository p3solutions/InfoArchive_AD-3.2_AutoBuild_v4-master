/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.server.operation;

import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.library.basic.AbstractSingleStoreOperation;
import com.emc.documentum.xml.dds.persistence.Store;

public class CheckHasRetiredOperation extends AbstractSingleStoreOperation<Boolean> {

  private String id;

  public CheckHasRetiredOperation(Store store, String id) throws OperationException {
    declareStore(store);
    this.id = id;
  }

  @Override
  public String getExecutableClassName() {
    return CheckHasRetiredOperationExecutable.class.getName();
  }

  @Override
  public boolean isReadOnly(String storeAlias) {
    return true;
  }

  @Override
  public String getId() {
    return id;
  }

}
