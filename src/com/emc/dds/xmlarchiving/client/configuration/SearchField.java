/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client.configuration;

/**
 * Class representing a field in a search form. WARNING: This class is experimental and may change
 * in future DDS releases.
 */
public class SearchField {

  private final String label;
  private final String xquery;
  private final String flexibleCode;
  private final String operator;
  private final boolean fullText;
  private final boolean metadata;
  private final boolean Variable;
  private final String type;

  public SearchField(boolean fullText, String label, String xquery, String flexibleCode,
      boolean metadata, String operator, String type, boolean Variable) {
    super();
    this.fullText = fullText;
    this.label = label;
    this.xquery = xquery;
    this.flexibleCode = flexibleCode;
    this.metadata = metadata;
    this.operator = operator;
    this.type = type;
    this.Variable = Variable;
  }

  public String getLabel() {
    return label;
  }

  public String getXQuery() {
    return xquery;
  }

  public String getFlexibleCode() {
    return flexibleCode;
  }

  public boolean isFullText() {
    return fullText;
  }

  public boolean isMetadata() {
    return metadata;
  }

  public boolean addAsVariable() {
    return Variable;
  }

  public String getType() {
    return type;
  }

  public String getOperator() {
    if (operator == null || operator.equals("")) {
      return "=";
    }
    return operator;
  }
}
