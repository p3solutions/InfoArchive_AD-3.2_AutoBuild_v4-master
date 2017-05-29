/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client.rpc;

import com.emc.documentum.xml.gwt.client.rpc.SerializableException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * RPC service for LDM operations.
 */
@RemoteServiceRelativePath("LDMService")
public interface LDMService extends RemoteService {

  Boolean handleXMLArchivingOperation(String input, String xqueryName, String user)
      throws SerializableException;
}
