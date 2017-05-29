/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * RPC service for LDM operations.
 */
public interface LDMServiceAsync {

  void handleXMLArchivingOperation(String input, String xqueryName, String user,
      AsyncCallback<Boolean> callback);
}
