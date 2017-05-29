/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client.configuration;

/**
 * Class representing a search result item of the search. WARNING: This class is experimental and
 * may change in future DDS releases.
 */
public class SearchResultItem {

  private final String label;
  private final boolean dateInMilliseconds;

  public SearchResultItem(String label, boolean dateInMilliseconds) {
    super();
    this.label = label;
    this.dateInMilliseconds = dateInMilliseconds;
  }

  public String getLabel() {
    return label;
  }

  public boolean isDateInMilliseconds() {
    return dateInMilliseconds;
  }
}
