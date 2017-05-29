/***************************************************************************************************
 * Copyright (c) 2009, 2011 EMC Corporation All Rights Reserved
 **************************************************************************************************/
package com.emc.dds.xmlarchiving.client.event;

import java.util.Map;

/**
 * Event fired when a search is submitted. WARNING: This class is experimental and may change in
 * future DDS releases.
 */
public class SearchSubmitEvent implements ApplicationEvent {

  private Map<String, String> fields;

  /**
   * Constructs a search submit event.
   * @param fields the search fields used in the search
   */
  public SearchSubmitEvent(Map<String, String> fields) {
    super();
    this.fields = fields;
  }

  /**
   * Returns the type of the event.
   * @return the type of the event.
   */
  @Override
  public int getType() {
    return SEARCH_SUBMIT_EVENT;
  }

  /**
   * Returns the key/value pairs of the search fields used.
   * @return the key/value pairs of the search fields used.
   */
  public Map<String, String> getFields() {
    return this.fields;
  }
}
