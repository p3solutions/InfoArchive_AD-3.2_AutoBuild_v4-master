/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client.configuration;

import java.util.Map;

public class NestedSearch {

  private String name;

  private String label;

  private String nodeId;

  private String description;

  private String configurationId;

  private Map<String, String> fields;

  private String transformationId;

  public NestedSearch(String name, String label, String description, String nodeId,
      String configurationId, Map<String, String> fields, String transformationId) {
    super();
    this.name = name;
    this.label = label;
    this.description = description;
    this.nodeId = nodeId;
    this.fields = fields;
    this.configurationId = configurationId;
    this.transformationId = transformationId;
  }

  public String getName() {
    return name;
  }

  public String getLabel() {
    return label;
  }

  public String getDescription() {
    return description;
  }

  public String getNodeId() {
    return nodeId;
  }

  public Map<String, String> getFields() {
    return fields;
  }

  public String getConfigurationId() {
    return configurationId;
  }

  public String getTransformationId() {
    return transformationId;
  }

}
