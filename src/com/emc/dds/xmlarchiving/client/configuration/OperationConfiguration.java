/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client.configuration;

import java.util.List;
import java.util.Map;

public class OperationConfiguration {

  private String xformURI;
  private String dialogTitle;
  private String buttonLabel;
  private String message;
  private String configurationId;
  private String sourceResultItemName;
  private Map<String, OperationField> fields;
  private List<String> xqueries;

  public OperationConfiguration(String xformURI, String buttonLabel, String dialogTitle,
      String message, Map<String, OperationField> fields, List<String> xqueries,
      String configurationId, String sourceResultItemName) {
    super();
    this.xformURI = xformURI;
    this.buttonLabel = buttonLabel;
    this.dialogTitle = dialogTitle;
    this.message = message;
    this.fields = fields;
    this.xqueries = xqueries;
    this.configurationId = configurationId;
    this.sourceResultItemName = sourceResultItemName;
  }

  public String getXformURI() {
    return xformURI;
  }

  public String getDialogTitle() {
    return dialogTitle;
  }

  public String getButtonLabel() {
    return buttonLabel;
  }

  public String getMessage() {
    return message;
  }

  public String getConfigurationId() {
    return configurationId;
  }

  public String getSourceResultItemName() {
    return sourceResultItemName;
  }

  public Map<String, OperationField> getFields() {
    return fields;
  }

  public List<String> getXqueries() {
    return xqueries;
  }

  public boolean isMessage() {
    return message != null && message.length() > 0;
  }
}
