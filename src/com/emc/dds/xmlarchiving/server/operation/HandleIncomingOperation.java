/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.server.operation;

import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.library.basic.AbstractSingleStoreOperation;
import com.emc.documentum.xml.dds.persistence.Store;

public class HandleIncomingOperation extends AbstractSingleStoreOperation<Object> {

  private String id;
  private String sharedLocation;
  private String xmlextensions;
  private int maxThreads;

  public HandleIncomingOperation(Store store, String id, String sharedLocation,
      String xmlextensions, int maxThreads) throws OperationException {
    declareStore(store);
    this.id = id;
    this.sharedLocation = sharedLocation;
    this.xmlextensions = xmlextensions;
    this.maxThreads = maxThreads;
  }

  @Override
  public String getExecutableClassName() {
    return HandleIncomingOperationExecutable.class.getName();
  }

  @Override
  public boolean isReadOnly(String storeAlias) {
    return false;
  }

  @Override
  public String getId() {
    return id;
  }

  public String getSharedLocation() {
    return sharedLocation;
  }

  public String getXmlextensions() {
    return xmlextensions;
  }

  public int getMaxThreads() {
    return maxThreads;
  }

}
