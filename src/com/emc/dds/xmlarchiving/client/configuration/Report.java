/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client.configuration;

import java.util.Map;

public class Report {

  private String id;

  private String label;

  private String description;

  private String configurationId;

  private String query;

  private Map<String, String> fields;

  public Report(String id, String label, String description, String configurationId, String query,
      Map<String, String> fields) {
    super();
    this.id = id;
    this.label = label;
    this.description = description;
    this.configurationId = configurationId;
    this.query = query;
    this.fields = fields;
  }

  public String getId() {
    return id;
  }

  public String getLabel() {
    return label;
  }

  public String getDescription() {
    return description;
  }

  public String getQuery() {
    return query;
  }

  public String getConfigurationId() {
    return configurationId;
  }

  public Map<String, String> getFields() {
    return fields;
  }
}
