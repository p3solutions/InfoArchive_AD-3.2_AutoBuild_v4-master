/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.server.operation;

import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.library.basic.AbstractSingleStoreOperation;
import com.emc.documentum.xml.dds.persistence.Store;

public class CreateMissingLocationOperation extends AbstractSingleStoreOperation<Object> {

  private String uri;

  public CreateMissingLocationOperation(Store store, String uri) throws OperationException {
    declareStore(store);
    this.uri = uri;
  }

  @Override
  public String getExecutableClassName() {
    return CreateMissingLocationOperationExecutable.class.getName();
  }

  @Override
  public boolean isReadOnly(String storeAlias) {
    return false;
  }

  public String getUri() {
    return uri;
  }
}
