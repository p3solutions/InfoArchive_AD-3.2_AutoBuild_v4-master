/*******************************************************************************
 * Copyright (c) 2015 EMC Corporation. All Rights Reserved.
 *******************************************************************************/
package com.emc.dds.xmlarchiving.client.event;

/**
 * Event fired when a language is changed. WARNING: This class is experimental and may change in
 * future DDS releases.
 */
public class LanguageChangedEvent implements ApplicationEvent {

  private String oldLocale;
  private String newLocale;

  /**
   * Constructs a language changed event.
   * @param newLocale the locale after it was changed.
   * @param oldLocale the locale before it was changed.
   */
  public LanguageChangedEvent(String newLocale, String oldLocale) {
    super();
    this.newLocale = newLocale;
    this.oldLocale = oldLocale;
  }

  /**
   * Returns the type of the event.
   * @return the type of the event.
   */
  @Override
  public int getType() {
    return LANGUAGE_CHANGED_EVENT;
  }

  /**
   * Returns the locale before it was changed.
   * @return the locale before it was changed.
   */
  public String getOldLocale() {
    return oldLocale;
  }

  /**
   * Returns the locale after it was changed.
   * @return the locale after it was changed.
   */
  public String getNewLocale() {
    return newLocale;
  }
}
