/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client.configuration;

/**
 * A search setting object associates a name with a {@link SearchConfiguration} object.
 */
public class SearchSetting {

  private final SearchConfigurations searchConfigurations;
  private final String name;
  private final String searchConfigurationId;

  public SearchSetting(SearchConfigurations searchConfigurations, String name,
      String searchConfigurationId) {

    super();
    this.searchConfigurations = searchConfigurations;
    this.name = name;
    this.searchConfigurationId = searchConfigurationId;
  }

  public String getName() {
    return name;
  }

  public String getSearchConfigurationId() {
    return searchConfigurationId;
  }

  public SearchConfiguration getSearchConfiguration() {
    return searchConfigurations.get(searchConfigurationId);
  }
}
