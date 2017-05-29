/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client.authorization;

public class Restriction {

  public enum RestrictionType {
    NODE,
    OPERATION,
    FIELD
  }

  private RestrictionType type;

  private String value;

  public Restriction(RestrictionType type, String value) {
    super();
    this.type = type;
    this.value = value;
  }

  public RestrictionType getType() {
    return type;
  }

  public String getValue() {
    return value;
  }
}
