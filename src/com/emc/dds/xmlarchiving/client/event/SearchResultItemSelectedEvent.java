/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client.event;

/**
 * Event fired when a search result item is selected. WARNING: This class is experimental and may
 * change in future DDS releases.
 */
public class SearchResultItemSelectedEvent implements ApplicationEvent {

  private String uri;
  private String schemaId;
  private String title;
  private String locale;

  /**
   * Constructs a search result item selected event.
   * @param uri the DDS uri of the selected item
   * @param schemaId the schema id of the selected item
   * @param title the title of the selected item
   * @param dataset the data set of the selected item
   * @param locale the locale of the selected item
   */
  public SearchResultItemSelectedEvent(String uri, String schemaId, String title, String locale) {
    super();
    this.uri = uri;
    this.schemaId = schemaId;
    this.title = title;
    this.locale = locale;
  }

  /**
   * Returns the type of the event.
   * @returns the type of the event.
   */
  @Override
  public int getType() {
    return SEARCH_RESULT_ITEM_SELECTED_EVENT;
  }

  /**
   * Returns the DDS uri of the selected item.
   * @return the DDS uri of the selected item.
   */
  public String getUri() {
    return uri;
  }

  /**
   * Returns the schema ID of the selected item.
   * @return the schema ID of the selected item.
   */
  public String getSchemaId() {
    return schemaId;
  }

  /**
   * Returns the title of the selected item.
   * @return the title of the selected item.
   */
  public String getTitle() {
    return title;
  }

  /**
   * Returns the locale of the selected item.
   * @return the locale of the selected item.
   */
  public String getLocale() {
    return locale;
  }

}
