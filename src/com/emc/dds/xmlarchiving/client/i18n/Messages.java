/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client.i18n;

/**
 * Messages for the template application.
 */
public interface Messages extends com.emc.documentum.xml.dds.gwt.client.i18n.DDSMessages {

  String loginFailed();

  String roleNotFoundError(String roleId);

  String loginFailedError();

  String noContentViewSettingFound(String schemaId);

  String unknownOperation(String operation);

  String unexpectedError(String error);

  String noDateAndDuration();

  String noURIFound();
}
