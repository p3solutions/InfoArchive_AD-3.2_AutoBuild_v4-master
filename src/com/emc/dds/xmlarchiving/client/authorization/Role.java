/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client.authorization;

import java.util.List;

import com.emc.dds.xmlarchiving.client.authorization.Restriction.RestrictionType;

public class Role {

  private List<Restriction> restrictions;

  public Role(List<Restriction> restrictions) {
    super();
    this.restrictions = restrictions;
  }

  private boolean hasAuthorization(Restriction.RestrictionType restrictionType, String value) {
    for (Restriction restriction : restrictions) {
      if (restriction.getType() == restrictionType && value.equals(restriction.getValue())) {
        return false;
      }
    }
    return true;
  }

  public boolean hasNodeAuthorization(String node) {
    return hasAuthorization(Restriction.RestrictionType.NODE, node);
  }

  public boolean hasOperationAuthorization(String operation) {
    return hasAuthorization(Restriction.RestrictionType.OPERATION, operation);
  }

  public boolean hasFieldAuthorization(String field) {
    return hasAuthorization(Restriction.RestrictionType.FIELD, field);
  }

  public String getUnauthorizedFields() {
    StringBuffer sb = new StringBuffer();
    for (Restriction restriction : restrictions) {
      if (restriction.getType() == RestrictionType.FIELD) {
        if (sb.length() > 0) {
          sb.append(", ");
        }
        sb.append(restriction.getValue());
      }
    }
    return sb.toString();
  }
}
