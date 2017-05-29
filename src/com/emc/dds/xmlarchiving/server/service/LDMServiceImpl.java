/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.server.service;

import com.emc.dds.xmlarchiving.client.rpc.LDMService;
import com.emc.dds.xmlarchiving.server.operation.HandleXMLArchivingOperation;
import com.emc.documentum.xml.dds.gwt.server.AbstractDDSService;
import com.emc.documentum.xml.dds.gwt.server.serialization.ExceptionSerializer;
import com.emc.documentum.xml.dds.logging.LogCenter;
import com.emc.documentum.xml.gwt.client.rpc.SerializableException;

/**
 * Implementation of {@link DataModuleService}.
 */
@SuppressWarnings("serial")
public class LDMServiceImpl extends AbstractDDSService implements LDMService {

  @Override
  public Boolean handleXMLArchivingOperation(String input, String operationName, String user)
      throws SerializableException {
    try {
      this.execute(new HandleXMLArchivingOperation(getApplication().getMainStore(), input,
          operationName, user));
    } catch (Exception e) {
      LogCenter.exception(this, e);
      throw ExceptionSerializer.serialize(e);
    }
    return Boolean.TRUE;
  }

}
