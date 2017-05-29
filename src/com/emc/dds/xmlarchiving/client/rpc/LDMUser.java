/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client.rpc;

import com.emc.documentum.xml.dds.gwt.client.rpc.application.SerializableUserObject;

public class LDMUser extends SerializableUserObject {

  /**
   * Default Serial Version UID.
   */
  private static final long serialVersionUID = 1L;

  private String roleId;

  public String getRoleId() {
    return roleId;
  }

  public void setRoleId(String roleId) {
    this.roleId = roleId;
  }
}
