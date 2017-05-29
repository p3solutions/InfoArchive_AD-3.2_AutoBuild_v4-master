/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.server.operation;

import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.library.basic.AbstractSingleStoreOperation;
import com.emc.documentum.xml.dds.persistence.Store;

public class CreateDetachableLibraryOperation extends AbstractSingleStoreOperation<Object> {

  private String parentlibraryPath;
  private String libraryName;
  private boolean createIndexes;

  public CreateDetachableLibraryOperation(Store store, String parentlibraryPath,
      String libraryName, boolean createIndexes) throws OperationException {
    super();
    declareStore(store);
    this.parentlibraryPath = parentlibraryPath;
    this.libraryName = libraryName;
    this.createIndexes = createIndexes;
  }

  @Override
  public String getExecutableClassName() {
    return CreateDetachableLibraryOperationExecutable.class.getName();
  }

  @Override
  public boolean isReadOnly(String storeAlias) {
    return false;
  }

  public String getParentlibraryPath() {
    return parentlibraryPath;
  }

  public String getLibraryName() {
    return libraryName;
  }

  public boolean isCreateIndexes() {
    return createIndexes;
  }
}
