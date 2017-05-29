/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client.event;

import com.emc.dds.xmlarchiving.client.configuration.SearchSetting;

/**
 * Event fired when selection is changed. Data sets may have different locales, therefore a data set
 * change may cause a locale change. WARNING: This class is experimental and may change in future
 * DDS releases.
 */
public class SearchSelectedEvent implements ApplicationEvent {

  private final SearchSetting searchSetting;

  /**
   * Constructs a data set change event.
   * @param newDataSet the name of the data set after it was changed.
   * @param oldDataSet the name of the data set before it was changed.
   * @param newLocale the locale before the data set was changed.
   * @param oldLocale the locale after the data set was changed.
   */
  public SearchSelectedEvent(SearchSetting searchSetting) {
    this.searchSetting = searchSetting;
  }

  /**
   * Returns the type of the event.
   */
  @Override
  public int getType() {
    return SEARCH_SELECTED_EVENT;
  }

  public SearchSetting getSearchSetting() {
    return searchSetting;
  }
}
