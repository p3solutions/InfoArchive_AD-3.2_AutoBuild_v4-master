/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.server.operation;

import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.library.basic.AbstractSingleStoreOperation;
import com.emc.documentum.xml.dds.persistence.Store;

public class HandleXMLArchivingOperation extends AbstractSingleStoreOperation<Object> {

  private String input;
  private String xqueryName;
  private String user;

  public HandleXMLArchivingOperation(Store store, String input, String xqueryName, String user)
      throws OperationException {
    declareStore(store);
    this.input = input;
    this.xqueryName = xqueryName;
    this.user = user;
  }

  @Override
  public String getExecutableClassName() {
    return HandleXMLArchivingOperationExecutable.class.getName();
  }

  @Override
  public boolean isReadOnly(String storeAlias) {
    return false;
  }

  public String getInput() {
    return input;
  }

  public String getXQueryName() {
    return xqueryName;
  }

  public String getUser() {
    return user;
  }

}
